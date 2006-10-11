using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap11
{
	public class Chap1103 
	{   
    
		public Chap1103() 
		{
        
			Console.WriteLine("Chapter 11 example 3: named actions");
        
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        
			try 
			{
            
				// step 2: we create a writer that listens to the document
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1103.pdf", FileMode.Create));
				// step 3: we open the document
				document.Open();
				// step 4: we add some content
				String application = "c:/winnt/notepad.exe";
				Paragraph p = new Paragraph(new Chunk("Click to open " + application).SetAction(new PdfAction(application, null, null, null)));
				PdfPTable table = new PdfPTable(4);
				table.DefaultCell.HorizontalAlignment = Element.ALIGN_CENTER;
				table.AddCell(new Phrase(new Chunk("First Page").SetAction(new PdfAction(PdfAction.FIRSTPAGE))));
				table.AddCell(new Phrase(new Chunk("Prev Page").SetAction(new PdfAction(PdfAction.PREVPAGE))));
				table.AddCell(new Phrase(new Chunk("Next Page").SetAction(new PdfAction(PdfAction.NEXTPAGE))));
				table.AddCell(new Phrase(new Chunk("Last Page").SetAction(new PdfAction(PdfAction.LASTPAGE))));
				for (int k = 1; k <= 10; ++k) 
				{
					document.Add(new Paragraph("This is page " + k));
					document.Add(table);
					document.Add(p);
					document.NewPage();
				}
			}
			catch (Exception de) 
			{
				Console.Error.WriteLine(de.StackTrace);
				Console.Error.WriteLine(de.Message);
			}
        
			// step 5: we close the document
			document.Close();
		}
	}
}