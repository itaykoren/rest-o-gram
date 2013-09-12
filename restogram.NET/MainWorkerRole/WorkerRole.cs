using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Net;
using System.Threading;
using Microsoft.WindowsAzure;
using Microsoft.WindowsAzure.Diagnostics;
using Microsoft.WindowsAzure.ServiceRuntime;
using Microsoft.WindowsAzure.Storage;
using System.Drawing;
using Google.Apis.Taskqueue.v1beta2;
using System.Text;
using Google.Apis.Taskqueue.v1beta2.Data;
using Google;

namespace restogram.NET
{
    public class MainWorkerRole : RoleEntryPoint
    {
        public override void Run()
        {
            while (true)
            {
                var taskqueueService = TaskQueueProvider.GetService();

                var lease = taskqueueService.Tasks.Lease(Defs.Resources.PROJECT_NAME, Defs.Resources.TASKS_QUEUE_NAME,
                                                         Defs.Scheduling.MAX_LEASE_COUNT, Defs.Scheduling.LEASING_TIMEOUT);
                Trace.WriteLine("leasing");
                Tasks tasks = null;
                try
                {
                    tasks = lease.Fetch();
                }
                catch (GoogleApiException e)
                {
                    Sleep(e);
                    continue;
                }
                Trace.WriteLine("leased tasks");

                if (tasks == null || tasks.Items == null || tasks.Items.Count == 0)
                {
                    Sleep(null);
                    continue;
                }

                // processed leased tasks
                foreach (var task in tasks.Items)
                {
                    Trace.WriteLine("handling a task");
                    string decodedPayload = null;
                    try
                    {
                        decodedPayload = task.PayloadBase64.DecodeFrom64();
                    }
                    catch (FormatException e)
                    {
                        Trace.WriteLine("error while decoding payload");
                        Trace.TraceError(e.Message);
                        continue;
                    }
                    var sections = decodedPayload.Split(new char[] {';'}, 
                                                        StringSplitOptions.RemoveEmptyEntries);
                    if (sections.Length != 2)
                        continue;
                    var venueId = sections[0];
                    var photoTasks = sections[1];
                    var idUrlPairs = photoTasks.Split(new char[] {','}, StringSplitOptions.RemoveEmptyEntries);
                    var rulesResult = new StringBuilder(venueId + ";");
                    var idToUrlMapping = Enumerable.Range(0, idUrlPairs.Length / 2)
                                            .ToDictionary(x => idUrlPairs[2 * x], x => idUrlPairs[2 * x + 1]);

                    // parallel/sequential handling code
                    var idToRuleMapping = 
                        idToUrlMapping.ToDictionary<KeyValuePair<String,String>,String, bool>(
                            (idToUrl) => idToUrl.Key,
                            (idToUrl) =>
                            {
                                var bitmap = ImageDownloader.DownloadRemoteImage(idToUrl.Value);
                                if (bitmap == null)
                                    return false;
                                try
                                {
                                    return !FaceDetector.ContainsFaces(bitmap);
                                }
                                catch (Exception e)
                                {
                                    Trace.WriteLine("an error occured while processing bitmap : " + idToUrl.Value);
                                    Trace.WriteLine(e.ToString());
                                    return false;
                                }
                                finally
                                {
                                    bitmap.Dispose();
                                }
                            });

                    // construct payload string
                    foreach (var idToRule in idToRuleMapping)
                    {
                        rulesResult.Append(idToRule.Key);
                        rulesResult.Append(",");
                        rulesResult.Append(idToRule.Value);
                        rulesResult.Append(",");
                    }
                    rulesResult = rulesResult.Remove(rulesResult.Length - 1, 1);

                    Trace.WriteLine("task executed");

                    // deletes executed task from queue
                    Trace.WriteLine("deleting task from queue");
                    var deleteTask = taskqueueService.Tasks.Delete(Defs.Resources.PROJECT_NAME, Defs.Resources.TASKS_QUEUE_NAME, task.Id);

                    String error;

                    try
                    {
                        error = deleteTask.Fetch();
                    }
                    catch (GoogleApiException e)
                    {
                        Sleep(e);
                        try
                        {
                            error = deleteTask.Fetch();
                        }
                        catch (GoogleApiException e2)
                        {
                            Sleep(e2);
                            continue;
                        }
                    }

                    if (String.IsNullOrWhiteSpace(error))
                        Trace.WriteLine("deleted task");
                    else
                        Trace.WriteLine("delete task - failed: "+error);

                    // inserting result to results queue
                    Trace.WriteLine("inserting result to queue");
                    var resultTask = new Task();
                    resultTask.QueueName = Defs.Resources.RESULTS_QUEUE_NAME;
                    resultTask.PayloadBase64 = rulesResult.ToString().EncodeTo64();
                    var insertResult = taskqueueService.Tasks.Insert(resultTask, Defs.Resources.PROJECT_NAME, 
                                                                        Defs.Resources.RESULTS_QUEUE_NAME);

                    Task insertedTask;
                    try
                    {
                        insertedTask = insertResult.Fetch();
                    }
                    catch (GoogleApiException e)
                    {
                        Sleep(e);
                        try
                        {
                            insertedTask = insertResult.Fetch();
                        }
                        catch (GoogleApiException e2)
                        {
                            Sleep(e2);
                            continue;
                        }
                    }

                    if (insertedTask != null)
                        Trace.WriteLine("inserted result to queue");
                    else
                        Trace.WriteLine("inserting result task to queue - failed");

                    // inform GAE - task executed
                    try
                    {
                        // fire and forget...
                        new System.Net.WebClient().DownloadStringAsync(Defs.Resources.NOTIFICATION_URL);
                    }
                    catch (WebException)
                    {
                        Trace.WriteLine("cannot infrom GAE");
                    }
                }
            }
        }

        private static void Sleep(GoogleApiException e)
        {
            if (e != null)
            {
                Trace.TraceError("msg: " + e.Message);
                Trace.TraceError("stack trace: " + e.StackTrace);
            }
            Trace.WriteLine("error while fetching tasks");
            Trace.WriteLine("going to sleep..zzZ");
            Thread.Sleep(Defs.Scheduling.IDLE_SLEEP_TIME);
        }


        public override bool OnStart()
        {
            // Set the maximum number of concurrent connections 
            ServicePointManager.DefaultConnectionLimit = 12;
            return base.OnStart();
        }
    }
}