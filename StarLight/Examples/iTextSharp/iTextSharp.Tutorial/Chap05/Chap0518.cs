using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap05
{
	/// <summary>
	/// Chap0518 的摘要说明。
	/// </summary>
	public class Chap0518
	{
		public Chap0518()
		{
			Console.WriteLine("Chapter 5 example 18: PdfPTable");
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4.Rotate(), 10, 10, 10, 10);
			try 
			{
				// step 2: we create a writer that listens to the document
				PdfWriter.GetInstance(document, new FileStream("Chap0518.pdf", FileMode.Create));
				// step 3: we open the document
				document.Open();
				// step 4: we add content to the document (this happens in a seperate method)
				loadDocument(document);
			}
			catch (Exception e2) 
			{
				Console.WriteLine(e2);
			}
			// step 5: we close the document
			document.Close();
		}
    
		public static void loadDocument(Document document) 
		{
			String[] bogusData = { "M0065920",
									 "SL",
									 "FR86000P",
									 "PCGOLD",
									 "119000",
									 "96 06",
									 "2001-08-13",
									 "4350",
									 "6011648299",
									 "FLFLMTGP",
									 "153",
									 "119000.00"
								 };
			int NumColumns = 12;
			try 
			{
				// we add some meta information to the document
            
				PdfPTable datatable = new PdfPTable(NumColumns);
            
				datatable.DefaultCell.Padding = 3;
				float[] headerwidths = {9, 4, 8, 10, 8, 11, 9, 7, 9, 10, 4, 10}; // percentage
				datatable.SetWidths(headerwidths);
				datatable.WidthPercentage = 100; // percentage
            
				datatable.DefaultCell.BorderWidth = 2;
				datatable.DefaultCell.HorizontalAlignment = Element.ALIGN_CENTER;
				datatable.AddCell("Clock #");
				datatable.AddCell("Trans Type");
				datatable.AddCell("Cusip");
				datatable.AddCell("Long Name");
				datatable.AddCell("Quantity");
				datatable.AddCell("Fraction Price");
				datatable.AddCell("Settle Date");
				datatable.AddCell("Portfolio");
				datatable.AddCell("ADP Number");
				datatable.AddCell("Account ID");
				datatable.AddCell("Reg Rep ID");
				datatable.AddCell("Amt To Go ");
            
				datatable.HeaderRows = 1;  // this is the end of the table header
            
				datatable.DefaultCell.BorderWidth = 1;
            
				int max = 666;
				for (int i = 1; i < max; i++) 
				{
					if (i % 2 == 1) 
					{
						datatable.DefaultCell.GrayFill = 0.9f;
					}
					for (int x = 0; x < NumColumns; x++) 
					{
						datatable.AddCell(bogusData[x]);
					}
					if (i % 2 == 1) 
					{
						datatable.DefaultCell.GrayFill = 0.0f;
					}
				}
				document.Add(datatable);
			}
			catch(Exception e) 
			{
				Console.Error.WriteLine(e.StackTrace);
			}

		}
	}
}
