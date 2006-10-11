using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap01
{
	/// <summary>
	/// Chap0106 的摘要说明。
	/// </summary>
	public class Chap0106
	{
		public Chap0106()
		{
			Console.WriteLine("Chapter 1 example 6: Meta Information");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
            
				PdfWriter.GetInstance(document, new FileStream("Chap0106.pdf", FileMode.Create));
            
				// step 3: we add some metadata and open the document
            
				document.AddTitle("Hello World example");
				document.AddSubject("This example explains step 6 in Chapter 1");
				document.AddKeywords("Metadata, iText, step 6, tutorial");
				document.AddCreator("My program using iText#");
				document.AddAuthor("Bruno Lowagie");
				document.AddHeader("Expires", "0");
				document.Open();
            
				// step 4: we add a paragraph to the document
				document.Add(new Paragraph("Hello World"));
            
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
