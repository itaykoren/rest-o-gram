using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace restogram.NET
{
    static class Defs
    {
        public static class Resources 
        {
            private static readonly string ACTUAL_PROJECT_NAME = "rest-o-gram";
            public static readonly string PROJECT_NAME = "s~" + ACTUAL_PROJECT_NAME;
            private static readonly string RULESET_SERVLET_NAME = "filter-rules";
            private static readonly string RULESET_UPDATE_OPERATION_NAME = "update";
            public static readonly Uri NOTIFICATION_URL = 
                new Uri(String.Format("http://{0}.{1}.appspot.com/{2}", RULESET_SERVLET_NAME, ACTUAL_PROJECT_NAME, RULESET_UPDATE_OPERATION_NAME));
            public static readonly string TASKS_QUEUE_NAME = "outgoing-queue";
            public static readonly string RESULTS_QUEUE_NAME = "incoming-queue";
        }

        public static class Scheduling
        {
            public static readonly int IDLE_SLEEP_TIME = 20 * 1000; // 20 secs
            public static readonly int MAX_LEASE_COUNT = 5;
            public static readonly int LEASING_TIMEOUT = 500;  // 500 secs
        }
    }
}
