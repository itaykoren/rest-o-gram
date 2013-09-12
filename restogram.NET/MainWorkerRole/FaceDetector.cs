using System;
using System.Drawing;
using Emgu.CV;
using Emgu.CV.Structure;

namespace restogram.NET
{
    static class FaceDetector
    {
        public static bool ContainsFaces(Bitmap bitmap)
        {
            using (var img = new Image<Gray, byte>(bitmap))
            {
                bool rotate;
                if (ContainsFaces(img, true, out rotate))
                    return true;
                else if (!rotate)
                    return false;

                var rotatedimg1 = img.Rotate(10, new Gray());

                if (ContainsFaces(img, false, out rotate))
                    return true;
                else if (!rotate)
                    return false;

                rotatedimg1 = img.Rotate(-10, new Gray());

                if (ContainsFaces(img, false, out rotate))
                    return true;

                return false;
            }
        }

        private static bool ContainsFaces(Image<Gray, byte> img, bool extensive, out bool rotate)
        {
            var maxSize = Math.Min(img.Width, img.Height);
            var faces = lbpCascade.DetectMultiScale(img, 1.1, 2, new Size(24, 24), new Size(maxSize, maxSize));

            foreach (var currFace in faces)
            {
                // crop curr face
                var currFaceImg = img.Copy(currFace);

                // verify mouth
                if (VerifyFace(currFaceImg, haarMouthCascade, 4, new Size(25, 15)))
                {
                    rotate = false;
                    return true;
                }

                // verify mouth
                if (VerifyFace(currFaceImg, haarFontalEyesCascade, 2, new Size(35, 16)))
                {
                    rotate = false;
                    return true;
                }
            }

            if (faces.Length != 0)
            {
                int haarProfileSensetivity = 4;
                var eyePairs = haarEyeGlassesCascade.DetectMultiScale(img, 1.1, 1, new Size(20, 20), new Size(maxSize, maxSize));

                foreach (var currFace in faces)
                    foreach (var currEyePair in eyePairs)
                        if (currFace.IntersectsWith(currEyePair))
                        {
                            rotate = false;
                            return true;
                        }

                // re-verify face
                var faces2 = haarAlt2Cascade.DetectMultiScale(img, 1.1, 4, new Size(20, 20), new Size(maxSize, maxSize));

                foreach (var currFace in faces2)
                    foreach (var currEyePair in eyePairs)
                        if (currFace.IntersectsWith(currEyePair))
                        {
                            rotate = false;
                            return true;
                        }

                if (faces2.Length != 0)
                    --haarProfileSensetivity;

                foreach (var currFace in faces2)
                {
                    // crop curr face
                    var currFaceImg = img.Copy(currFace);

                    // verify mouth
                    if (VerifyFace(currFaceImg, haarMouthCascade, 4, new Size(25, 15)))
                    {
                        rotate = false;
                        return true;
                    }
                }

                if (extensive)
                {
                    foreach (var currFace in faces)
                        foreach (var currFace2 in faces2)
                            if (currFace.IntersectsWith(currFace2))
                            {
                                if (currFace.IntersectsWith(currFace2, 0.75))
                                {
                                    rotate = false;
                                    return true;
                                }

                                if (haarProfileSensetivity > 2)
                                    --haarProfileSensetivity;
                            }

                    faces2 = haarProfileCascade.DetectMultiScale(img, 1.1, haarProfileSensetivity, new Size(20, 20), new Size(maxSize, maxSize));

                    if (faces2.Length != 0)
                    {
                        rotate = false;
                        return true;
                    }
                }

                rotate = false;
                return false;
            }
            else if (extensive)
            {
                    var eyePairs2 = haarEyesBigCascade.DetectMultiScale(img, 1.1, 2, new Size(45, 11), new Size(maxSize, maxSize));

                    if (eyePairs2.Length != 0)
                    {
                        rotate = false;
                        return true;
                    }

                    eyePairs2 = haarEyeGlassesCascade.DetectMultiScale(img, 1.1, 2, new Size(20, 20), new Size(maxSize, maxSize));

                    if (eyePairs2.Length != 0)
                    {
                        rotate = false;
                        return true;
                    }

                    rotate = false;
                    return false;
            }

            rotate = true;
            return false;
        }

        private static bool VerifyFace(Image<Gray, byte> currFaceImg, CascadeClassifier classifier, 
                                       int sens, Size min)
        {
            var maxSize = Math.Min(currFaceImg.Width, currFaceImg.Height);
            var total = min.Width + min.Height;
            var objects = classifier.DetectMultiScale(currFaceImg, 1.1, sens, min,
                                                new Size(maxSize * min.Width / total,
                                                         maxSize * min.Height / total));
            return objects.Length != 0;
        }

        private static CascadeClassifier lbpCascade =
            new CascadeClassifier("./haarcascade/lbpcascade_frontalface.xml");
        private static CascadeClassifier haarMouthCascade =
            new CascadeClassifier("./haarcascade/haarcascade_mcs_mouth.xml");
        private static CascadeClassifier haarFontalEyesCascade =
            new CascadeClassifier("./haarcascade/haarcascade_frontal_eyes.xml");
        private static CascadeClassifier haarEyeGlassesCascade =
            new CascadeClassifier("./haarcascade/haarcascade_eye_tree_eyeglasses.xml");
        private static CascadeClassifier haarAlt2Cascade =
            new CascadeClassifier("./haarcascade/haarcascade_frontalface_alt2.xml");
        private static CascadeClassifier haarProfileCascade =
            new CascadeClassifier("./haarcascade/haarcascade_profileface.xml");
        private static CascadeClassifier haarEyesBigCascade =
            new CascadeClassifier("./haarcascade/haarcascade_mcs_eyepair_big.xml");

        
        
        
    }
}
