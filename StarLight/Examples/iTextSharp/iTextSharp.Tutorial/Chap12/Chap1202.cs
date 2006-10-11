using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap12
{
	class MyTableEvent : IPdfPTableEvent 
	{	
		public void TableLayout(PdfPTable table, float[][] width, float[] heights, int headerRows, int rowStart, PdfContentByte[] canvases) 
		{
			float[] widths = width[0];
			PdfContentByte cb = canvases[PdfPTable.TEXTCANVAS];
			cb.SaveState();
			cb.SetLineWidth(2);
			cb.SetRGBColorStroke(255, 0, 0);
			cb.Rectangle(widths[0], heights[heights.Length - 1], widths[widths.Length - 1] - widths[0], heights[0] - heights[heights.Length - 1]);
			cb.Stroke();
			if (headerRows > 0) 
			{
				float headerHeight = heights[0];
				for (int k = 0; k < headerRows; ++k)
					headerHeight += heights[k];
				cb.SetRGBColorStroke(0, 0, 255);
				cb.Rectangle(widths[0], heights[headerRows], widths[widths.Length - 1] - widths[0], heights[0] - heights[headerRows]);
				cb.Stroke();
			}
			cb.RestoreState();
			cb = canvases[PdfPTable.BASECANVAS];
			cb.SaveState();
			cb.SetLineWidth(.5f);
			Random r = new Random();
			for (int line = 0; line < heights.Length - 1; ++line) 
			{
				widths = width[line];
				for (int col = 0; col < widths.Length - 1; ++col) 
				{
					if (line == 0 && col == 0)
						cb.SetAction(new PdfAction("http://www.Geocities.Com/itextpdf"),
							widths[col], heights[line + 1], widths[col + 1], heights[line]);
					cb.SetRGBColorStrokeF((float)r.NextDouble(), (float)r.NextDouble(), (float)r.NextDouble());
					cb.MoveTo(widths[col], heights[line]);
					cb.LineTo(widths[col + 1], heights[line]);
					cb.Stroke();
					cb.SetRGBColorStrokeF((float)r.NextDouble(), (float)r.NextDouble(), (float)r.NextDouble());
					cb.MoveTo(widths[col], heights[line]);
					cb.LineTo(widths[col], heights[line + 1]);
					cb.Stroke();
				}
			}
			cb.RestoreState();
		}
	}

	public class Chap1202 
	{
    
		public Chap1202() 
		{
        
			Console.WriteLine("Chapter 12 example 2: Table events");
        
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			try 
			{
				// step 2: we create a writer that listens to the document
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1202.pdf", FileMode.Create));
				// step 3: we open the document
				document.Open();
				// step 4: we add some content
				BaseFont bf = BaseFont.CreateFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
				// table 1
				PdfPTable table = new PdfPTable(4);
				table.DefaultCell.Border = Rectangle.NO_BORDER;
				for (int k = 0; k < 24; ++k) 
				{
					if (k != 0)
						table.AddCell("" + k);
					else
						table.AddCell("This is an URL");
				}
				MyTableEvent evnt = new MyTableEvent();
				table.TableEvent = evnt;
				table.TotalWidth = 300;
				// write table 1 at some position
				table.WriteSelectedRows(0, -1, 100, 600, writer.DirectContent);
				// add table 1 (default position)
				document.Add(table);
				document.NewPage();
				// table 2
				table = new PdfPTable(4);
				float fontSize = 12;
				table.DefaultCell.PaddingTop = bf.GetFontDescriptor(BaseFont.ASCENT, fontSize) - fontSize + 2;
				table.DefaultCell.Border = Rectangle.NO_BORDER;
				for (int k = 0; k < 500 * 4; ++k) 
				{
					if (k == 0)
						table.AddCell(new Phrase("This is an URL", new Font(bf, fontSize)));
					else
						table.AddCell(new Phrase("" + k, new Font(bf, fontSize)));
				}
				table.TableEvent = evnt;
				table.HeaderRows = 3;
				document.Add(table);
			}
			catch (Exception de) 
			{
				Console.Error.WriteLine(de.Message);
				Console.Error.WriteLine(de.StackTrace);
			}
			// step 5: close the document
			document.Close();
		}
	}
}