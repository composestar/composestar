using System;
using System.IO;
using System.Drawing.Imaging;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap06
{
	public class Chap0611 
	{
        
		public Chap0611() 
		{
			// creation of the document with a certain size and certain margins
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			//Document.compress = false;
			try 
			{
				// creation of the different writers
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap0611.pdf", FileMode.Create));

				System.Drawing.Bitmap bm = new System.Drawing.Bitmap("338814-00.tif");
				int total = bm.GetFrameCount(FrameDimension.Page);
                            
				Console.WriteLine("Number of images in this TIFF: " + total);

				// Which of the multiple images in the TIFF file do we want to load
				// 0 refers to the first, 1 to the second and so on.
				document.Open();
				PdfContentByte cb = writer.DirectContent;
				for (int k = 0; k < total; ++k) 
				{
					bm.SelectActiveFrame(FrameDimension.Page, k);
					Image img = Image.GetInstance(bm, null, true);
					img.ScalePercent(72f / 200f * 100);
					img.SetAbsolutePosition(0, 0);
					Console.WriteLine("Image: " + k);
					cb.AddImage(img);
					document.NewPage();
				}
				document.Close();
			}
			catch (Exception de) 
			{
				Console.Error.WriteLine(de.Message);
				Console.Error.WriteLine(de.StackTrace);
			}
		}
	}
}