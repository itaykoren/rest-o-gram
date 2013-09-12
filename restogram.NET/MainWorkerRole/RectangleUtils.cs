using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace restogram.NET
{
    static class RectangleUtils
    {
        public static bool IntersectsWith(this Rectangle rect, Rectangle rect2,  double threshold)
        {
            var intersection = new Rectangle(rect.Location, rect.Size);
            intersection.Intersect(rect2);
            return ((intersection.Width * intersection.Height) >= (rect.Width * rect.Height) * threshold);
        }
    }
}
