using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap05
{
	/// <summary>
	/// Chap0512 ??ժҪ˵????
	/// </summary>
	public class Chap0512
	{
		public Chap0512()
		{
			Console.WriteLine("Chapter 5 example 12: avoid cell splitting");
			// creation of the document with a certain size and certain margins
			Document document = new Document(PageSize.A4.Rotate(), 50, 50, 50, 50);
        
			try 
			{
				// creation of the different writers
				PdfWriter.GetInstance(document, new FileStream("Chap0512.pdf", FileMode.Create));
            
				// we add some meta information to the document
				document.AddAuthor("Gerald Henson");
				document.AddSubject("This is the result of a Test.");
            
				document.Open();
            
				Table datatable = new Table(10);
				datatable.CellsFitPage = true;
            
				datatable.Padding = 4;
				datatable.Spacing = 0;
				//datatable.setBorder(Rectangle.NO_BORDER);
				float[] headerwidths = {10, 24, 12, 12, 7, 7, 7, 7, 7, 7};
				datatable.Widths = headerwidths;
				datatable.WidthPercentage = 100;
            
				// the first cell spans 10 columns
				Cell cell = new Cell(new Phrase("Administration -System Users Report", FontFactory.GetFont(FontFactory.HELVETICA, 24, Font.BOLD)));
				cell.HorizontalAlignment = Element.ALIGN_CENTER;
				cell.Leading = 30;
				cell.Colspan = 10;
				cell.Border = Rectangle.NO_BORDER;
				cell.BackgroundColor = new Color(0xC0, 0xC0, 0xC0);
				datatable.AddCell(cell);
            
				// These cells span 2 rows
				datatable.DefaultCellBorderWidth = 2;
				datatable.DefaultHorizontalAlignment = 1;
				datatable.DefaultRowspan = 2;
				datatable.AddCell("User Id");
				datatable.AddCell(new Phrase("Name", FontFactory.GetFont(FontFactory.HELVETICA, 14, Font.BOLD)));
				datatable.AddCell("Company");
				datatable.AddCell("Department");
            
				// This cell spans the remaining 6 columns in 1 row
				datatable.DefaultRowspan = 1;
				datatable.DefaultColspan = 6;
				datatable.AddCell("Permissions");
            
				// These cells span 1 row and 1 column
				datatable.DefaultColspan = 1;
				datatable.AddCell("Admin");
				datatable.AddCell("Data");
				datatable.AddCell("Expl");
				datatable.AddCell("Prod");
				datatable.AddCell("Proj");
				datatable.AddCell("Online");
            
				// this is the end of the table header
				datatable.EndHeaders();
            
				datatable.DefaultCellBorderWidth = 1;
				datatable.DefaultRowspan = 1;
            
				for (int i = 1; i < 30; i++) 
				{
                
					datatable.DefaultHorizontalAlignment = Element.ALIGN_LEFT;
                
					datatable.AddCell("myUserId");
					datatable.AddCell("Somebody with a very, very, very, very, very, very, very, very, very, very, very, very, very, very, very, very, very, very, very, very, very, very, very, very, very, very, very long long name");
					datatable.AddCell("No Name Company");
					datatable.AddCell("D" + i);
                
					datatable.DefaultHorizontalAlignment = Element.ALIGN_CENTER;
					datatable.AddCell("No");
					datatable.AddCell("Yes");
					datatable.AddCell("No");
					datatable.AddCell("Yes");
					datatable.AddCell("No");
					datatable.AddCell("Yes");
                
				}
            
            
				document.Add(datatable);
			}
			catch(Exception e) 
			{
				Console.Error.WriteLine(e.StackTrace);
			}
        
			// we close the document
			document.Close();

		}
	}
}
