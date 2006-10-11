using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap06
{
	public class Chap0610 
	{
    
		public Chap0610() 
		{
        
			Console.WriteLine("Chapter 6 example 10: Images using System.Drawing.Bitmap!");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap0610.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we add content to the document
				for (int i = 0; i < 300; i++) 
				{
					document.Add(new Phrase("Who is this? "));
				}
				PdfContentByte cb = writer.DirectContent;
//				Image image = Image.GetInstance(new System.Drawing.Bitmap("h.gif"), null);
				Image image = Image.GetInstance("h.gif");
				image.SetAbsolutePosition(100, 200);
				cb.AddImage(image);
			}
			catch(DocumentException de) 
			{
				Console.WriteLine(de.Message);
			}
			catch(IOException ioe) 
			{
				Console.WriteLine(ioe.Message);
			}
        
			// step 5: we close the document
			document.Close();
		}
	}
}