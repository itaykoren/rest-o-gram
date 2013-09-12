using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace restogram.NET
{
    static class ImageDownloader
    {
        public static Bitmap DownloadRemoteImage(string uri)
        {
            HttpWebRequest request = null;
            HttpWebResponse response = null;
            try
            {
                request = (HttpWebRequest)WebRequest.Create(uri);
                response = (HttpWebResponse)request.GetResponse();
            }
            catch (Exception)
            {
                return null;
            }

            if (!response.HasImageResult())
                return null;

            try
            {
                using (var imgStream = response.GetResponseStream())
                {
                    return new Bitmap(imgStream);
                }
            }
            catch (Exception)
            {
                return null;
            }
            finally
            {
                response.Dispose();
            }
        }
    }
}
