using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap10
{
	public class Chap1004 
	{
    
		public Chap1004() 
		{
        
			Console.WriteLine("Chapter 10 example 4: Templates");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1004.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we grab the ContentByte and do some stuff with it
				PdfContentByte cb = writer.DirectContent;
            
				// we create a PdfTemplate
				PdfTemplate template = cb.CreateTemplate(50, 50);
				BaseFont bf = BaseFont.CreateFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				// we add a number of pages
				int i;
				for (i = 1; i < 5; i++) 
				{
					String text = "Page " + writer.PageNumber + " of ";
					float len = bf.GetWidthPoint(text, 12);
					cb.BeginText();
					cb.SetFontAndSize(bf, 12);
					cb.SetTextMatrix(280, 40);
					cb.ShowText(text);
					cb.EndText();
					cb.AddTemplate(template, 280 + len, 40);
					document.NewPage();
				}
				template.BeginText();
				template.SetFontAndSize(bf, 12);
				template.ShowText((writer.PageNumber - 1).ToString());
				template.EndText();
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