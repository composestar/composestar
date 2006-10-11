using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap09
{
	public class Chap0901 
	{
		public Chap0901() 
		{
        
			Console.WriteLine("Chapter 9 example 1: Other encodings");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0901.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we add content to the document
				BaseFont helvetica = BaseFont.CreateFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				Console.WriteLine(helvetica.PostscriptFontName);
				Font font = new Font(helvetica, 12, Font.NORMAL);
				Chunk chunk = new Chunk("Sponsor this example and send me 1\u20ac. These are some special characters: \u0152\u0153\u0160\u0161\u0178\u017D\u0192\u02DC\u2020\u2021\u2030", font);
				document.Add(chunk);
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