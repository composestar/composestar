using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap10
{
	public class Chap1010 
	{
    
		public Chap1010() 
		{
        
			Console.WriteLine("Chapter 10 example 10: nested PdfPTables");
        
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			try 
			{
				// step 2: we create a writer that listens to the document
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1010.pdf", FileMode.Create));
				// step 3: we open the document
				document.Open();
				// step 4: we add some content
				PdfPTable table = new PdfPTable(4);
				PdfPTable nested1 = new PdfPTable(2);
				nested1.AddCell("1.1");
				nested1.AddCell("1.2");
				PdfPTable nested2 = new PdfPTable(1);
				nested2.AddCell("2.1");
				nested2.AddCell("2.2");
				for (int k = 0; k < 24; ++k) 
				{
					if (k == 1) 
					{
						table.AddCell(nested1);
					}
					else if (k == 20) 
					{
						table.AddCell(nested2);
					}
					else 
					{
						table.AddCell("cell " + k);
					}
				}
				table.TotalWidth = 300;
				table.WriteSelectedRows(0, -1, 100, 600, writer.DirectContent);
				// step 5: we close the document
				document.Close();
			}
			catch (Exception de) 
			{
				Console.Error.WriteLine(de.Message);
				Console.Error.WriteLine(de.StackTrace);
			}
		}
	}
}