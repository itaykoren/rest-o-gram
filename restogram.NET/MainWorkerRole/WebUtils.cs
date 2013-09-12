using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace restogram.NET
{
    static class WebUtils
    {
        public static bool HasImageResult(this HttpWebResponse response)
        {
            return (response.StatusCode == HttpStatusCode.OK ||
                    response.StatusCode == HttpStatusCode.Moved ||
                    response.StatusCode == HttpStatusCode.Redirect) &&
                    response.ContentType.StartsWith("image", StringComparison.OrdinalIgnoreCase);
        }
    }
}
