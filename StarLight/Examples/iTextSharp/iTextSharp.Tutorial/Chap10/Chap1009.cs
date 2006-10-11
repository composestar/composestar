using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap10
{
	public class Chap1009 
	{
    
		public Chap1009() 
		{        
			Console.WriteLine("Chapter 10 example 9: a PdfPTable at an absolute position");
        
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			try 
			{
				// step 2:  we create a writer that listens to the document
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1009.pdf", FileMode.Create));
				// step 3: we open the document
				document.Open();
				// step 4: we add some content
				PdfPTable table = new PdfPTable(4);
				table.DefaultCell.Border = Rectangle.LEFT_BORDER | Rectangle.RIGHT_BORDER;
				for (int k = 0; k < 24; ++k) 
				{
					table.AddCell("cell " + k);
				}
				table.TotalWidth = 300;
				table.WriteSelectedRows(0, -1, 100, 600, writer.DirectContent);
				// step 5: we close the document
				document.Close();
			}
			catch (Exception de) 
			{
				Console.WriteLine(de.Message);
				Console.WriteLine(de.StackTrace);
			}
		}
	}
}