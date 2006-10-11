using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap03
{
	/// <summary>
	/// Chap0304 的摘要说明。
	/// </summary>
	public class Chap0304
	{
		public Chap0304()
		{
			Console.WriteLine("Chapter 3 example 4: annotations at absolute positions");
        
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			try 
			{
				// step 2:
				// we create a writer that listens to the document
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap0304.pdf", FileMode.Create));
				// step 3: we Open the document
				document.Open();
				// step 4: we Add some content
            
				PdfContentByte cb = writer.DirectContent;

				// draw a rectangle
				cb.SetRGBColorStroke(0x00, 0x00, 0xFF);
				cb.Rectangle(100, 700, 100, 100);
				cb.Stroke();
				Annotation annot = new Annotation(100f, 700f, 200f, 800f, "http://itextsharp.Sourceforge.net");
				document.Add(annot);
				cb.SetRGBColorStroke(0xFF, 0x00, 0x00);
				cb.Rectangle(200, 700, 100, 100);
				cb.Stroke();
				try 
				{
					document.Add(new Annotation(200f, 700f, 300f, 800f, new Uri("http://itextsharp.Sourceforge.net")));
				}
				catch 
				{
				}
				cb.SetRGBColorStroke(0x00, 0xFF, 0x00);
				cb.Rectangle(300, 700, 100, 100);
				cb.Stroke();
				document.Add(new Annotation(300f, 700f, 400f, 800f, "c:/winnt/notepad.exe", null, null, null));
				cb.SetRGBColorStroke(0x00, 0x00, 0xFF);
				cb.Rectangle(100, 500, 100, 100);
				cb.Stroke();
				document.Add(new Annotation("annotation", "This annotation is placed on an absolute position", 100f, 500f, 200f, 600f));
				cb.SetRGBColorStroke(0xFF, 0x00, 0x00);
				cb.Rectangle(200, 500, 100, 100);
				cb.Stroke();
				document.Add(new Annotation(200f, 500f, 300f, 600f, "Chap1102a.pdf", "test"));
				cb.SetRGBColorStroke(0x00, 0xFF, 0x00);
				cb.Rectangle(300, 500, 100, 100);
				cb.Stroke();
				document.Add(new Annotation(300f, 500f, 400f, 600f, "Chap1102b.pdf", 3));
			}
			catch (Exception de) 
			{
				Console.WriteLine(de.StackTrace);
			}
        
			// step 5: we close the document
			document.Close();

		}
	}
}
