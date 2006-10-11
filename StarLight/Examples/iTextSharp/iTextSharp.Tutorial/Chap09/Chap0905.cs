using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap09
{
	public class Chap0905 
	{
		public Chap0905() 
		{
        
			Console.WriteLine("Chapter 9 example 5: changing fontwidths");
        
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
            
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap0905.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we add content to the document
				BaseFont bf = BaseFont.CreateFont("Helvetica", "winansi", false, false, null, null);
				int[] widths = bf.Widths;
				for (int k = 0; k < widths.Length; ++k) 
				{
					if (widths[k] != 0)
						widths[k] = 1000;
				}
				bf.ForceWidthsOutput = true;
				document.Add(new Paragraph("A big text to show Helvetica with fixed width.", new Font(bf)));
        
			}
			catch (Exception de) 
			{
				Console.Error.WriteLine(de.StackTrace);
			}
			// step 5: we close the document
			document.Close();
		}
	}
}