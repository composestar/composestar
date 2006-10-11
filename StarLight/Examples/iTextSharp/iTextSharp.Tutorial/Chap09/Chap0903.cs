using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap09
{
	public class Chap0903 
	{
		public Chap0903() 
		{
        
			Console.WriteLine("Chapter 9 example 3: True Types (embedded)");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0903.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we add content to the document
				BaseFont bfComic = BaseFont.CreateFont("c:\\winnt\\fonts\\comic.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				Font font = new Font(bfComic, 12);
				String text1 = "This is the quite popular True Type font 'Comic'.";
				String text2 = "Some greek characters: \u0393\u0394\u03b6";
				String text3 = "Some cyrillic characters: \u0418\u044f";
				document.Add(new Paragraph(text1, font));
				document.Add(new Paragraph(text2, font));
				document.Add(new Paragraph(text3, font));
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