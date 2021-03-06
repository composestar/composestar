using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap11
{
	public class Chap1110 
	{
    
		public Chap1110() 
		{
        
			Console.WriteLine("Chapter 11 example 10: page labels");
        
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			try 
			{
				// step 2:
				// we create a writer that listens to the document
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1110.pdf", FileMode.Create));
				// step 3: we open the document
				document.Open();
				// step 4:
				// we add some content
				for (int k = 1; k <= 10; ++k) 
				{
					document.Add(new Paragraph("This document has the logical page numbers: i,ii,iii,iv,1,2,3,A-8,A-9,A-10\nReal page " + k));
					document.NewPage();
				}
				PdfPageLabels pageLabels = new PdfPageLabels();
				pageLabels.AddPageLabel(1, PdfPageLabels.LOWERCASE_ROMAN_NUMERALS);
				pageLabels.AddPageLabel(5, PdfPageLabels.DECIMAL_ARABIC_NUMERALS);
				pageLabels.AddPageLabel(8, PdfPageLabels.DECIMAL_ARABIC_NUMERALS, "A-", 8);
				writer.PageLabels = pageLabels;
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