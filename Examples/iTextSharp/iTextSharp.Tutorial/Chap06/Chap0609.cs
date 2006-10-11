using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap06
{
	public class Chap0609 
	{
    
		public Chap0609() 
		{
        
			Console.WriteLine("Chapter 6 example 9: bytes() / Raw Image");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
            
				PdfWriter.GetInstance(document, new FileStream("Chap0609.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we add content (example by Paulo Soares)
            
				// creation a jpeg passed as an array of bytes to the Image
				FileStream rf = new FileStream("mykids.jpg", FileMode.Open, FileAccess.Read);
				int size = (int)rf.Length;
				byte[] imext = new byte[size];
				rf.Read(imext, 0, size);
				rf.Close();
				Image img1 = Image.GetInstance(imext);
				img1.SetAbsolutePosition(50, 500);
				document.Add(img1);
            
				// creation of an image of 100 x 100 pixels (x 3 bytes for the Red, Green and Blue value)
				byte[] data = new byte[100*100*3];
				for (int k = 0; k < 100; ++k) 
				{
					for (int j = 0; j < 300; j += 3) 
					{
						data[k * 300 + j] = (byte)(255 * Math.Sin(j * .5 * Math.PI / 300));
						data[k * 300 + j + 1] = (byte)(256 - j * 256 / 300);
						data[k * 300 + j + 2] = (byte)(255 * Math.Cos(k * .5 * Math.PI / 100));
					}
				}
				Image img2 = Image.GetInstance(100, 100, 3, 8, data);
				img2.SetAbsolutePosition(200, 200);
				document.Add(img2);
			}
			catch(DocumentException de) 
			{
				Console.Error.WriteLine(de.Message);
			}
			catch(IOException ioe) 
			{
				Console.Error.WriteLine(ioe.Message);
			}
        
			// step 5: we close the document
			document.Close();
		}
	}
}