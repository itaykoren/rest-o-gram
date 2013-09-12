using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Google.Apis.Authentication.OAuth2;
using Google.Apis.Authentication.OAuth2.DotNetOpenAuth;
using Google.Apis.Util;
using Google.Apis.Taskqueue.v1beta2;
using System.Security.Cryptography.X509Certificates;
using DotNetOpenAuth.OAuth2;
using Google.Apis.Services;

namespace restogram.NET
{
    class TaskQueueProvider
    {
        public static TaskqueueService GetService()
        {
            if (taskQueueServices != null)
                return getRandomService();

            taskQueueServices  =  new TaskqueueService[credentialsCount];
            for (int  i = 0; i  < taskQueueServices.Length; ++i)
                taskQueueServices[i] = BuildService(SERVICE_ACCOUNT_EMAILS[i], 
                                       SERVICE_ACCOUNT_PKCS12_FILE_PATHS[i]);
            return getRandomService();
        }

        private static TaskqueueService getRandomService()
        {
            counter = (counter + 1) % credentialsCount;
            return taskQueueServices[counter];
        }

        private static TaskqueueService BuildService(string accountId, string accountKeyFilePath) 
        {
            X509Certificate2 certificate = new X509Certificate2(accountKeyFilePath, "notasecret",
                X509KeyStorageFlags.Exportable);

            var provider = new AssertionFlowClient(GoogleAuthenticationServer.Description, certificate)
            {
                ServiceAccountId = accountId,
                Scope = TaskqueueService.Scopes.Taskqueue.GetStringValue()
            };

            // uses a custom authernticator to avoid an SDK token refresh bug...
            var auth = new MyOAuth2Authenticator(provider, AssertionFlowClient.GetState);

            return new TaskqueueService((new BaseClientService.Initializer()
                                {
                                    Authenticator = auth
                                }));
        }

        private static int counter;
        private static readonly int credentialsCount = 3;
        private static TaskqueueService[] taskQueueServices;
        private static readonly string[] SERVICE_ACCOUNT_EMAILS = { "1068427818709-9f1h2fvdrtuopk94uvamdlimfjgg4b75@developer.gserviceaccount.com",
                                                                    "449239466454@developer.gserviceaccount.com",
                                                                     "313444126549@developer.gserviceaccount.com"};
        private static readonly string[] SERVICE_ACCOUNT_PKCS12_FILE_PATHS = { @".\cert\8bd34f48075f4cc2281dd2d039fc0629a8e69ed3-privatekey.p12",
                                                                               @".\cert\9923ae6fcb9e1c3b2f280d98c9d53596b38be073-privatekey.p12",
                                                                               @".\cert\87a641631054cb3630117a3e5a759157505e101d-privatekey.p12"};
    }
}
