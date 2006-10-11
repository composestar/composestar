using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap11
{
	public class Chap1109 
	{
    
		public Chap1109() 
		{
        
			Console.WriteLine("Chapter 11 example 9: outlines with actions");
        
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        
			try 
			{
				// step 2:
				// we create a writer that listens to the document
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1109.pdf", FileMode.Create));
				// step 3: we open the document
				document.Open();
				// step 4:
				// we add some content
				document.Add(new Paragraph("Outline action example"));
				// we add the outline
				PdfContentByte cb = writer.DirectContent;
				cb.AddOutline(new PdfOutline(cb.RootOutline, new PdfAction("http://www.Geocities.Com/itextpdf"), "http://www.Geocities.Com/itextpdf"));
				cb.AddOutline(new PdfOutline(cb.RootOutline, new PdfAction("http://iTextSharp.Sourceforge.Net"), "http://iTextSharp.Sourceforge.Net"));
				cb.AddOutline(new PdfOutline(cb.RootOutline, new PdfAction("Chap1102b.pdf", 3), "Chap1102b.pdf"));
			}
			catch (Exception de) 
			{
				Console.WriteLine(de.Message);
				Console.WriteLine(de.StackTrace);
			}
        
			// step 5: we close the document
			document.Close();
		}
	}
}