using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap11
{
	public class Chap1104 
	{
    
		public Chap1104() 
		{
        
			Console.WriteLine("Chapter 11 example 4: open action");
        
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			try 
			{
				// step 2: we create a writer that listens to the document
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1104.pdf", FileMode.Create));
				// step 3: we open the document
				document.Open();
				// step 4: we add some content
				document.Add(new Paragraph("Page 1"));
				document.NewPage();
				document.Add(new Paragraph("This PDF file jumps directly to page 2 when opened"));
				PdfContentByte cb = writer.DirectContent;
				cb.LocalDestination("page2", new PdfDestination(PdfDestination.XYZ, -1, 10000, 0));
				writer.SetOpenAction("page2");
			}
			catch (Exception de) 
			{
				Console.Error.WriteLine(de.Message);
				Console.Error.WriteLine(de.StackTrace);
			}
        
			// step 5: we close the document
			document.Close();
		}
	}
}