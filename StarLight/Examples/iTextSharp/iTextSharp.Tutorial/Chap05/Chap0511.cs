using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap05
{
	/// <summary>
	/// Chap0511 ??ժҪ˵????
	/// </summary>
	public class Chap0511
	{
		public Chap0511()
		{
			Console.WriteLine("Chapter 5 example 11: avoid table splitting");
			// step 1: creation of a document-object
			Document document = new Document();
			try 
			{
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0511.pdf", FileMode.Create));
				// step 3: we open the document
				document.Open();
				// step 4: we create a table and add it to the document
				Table table = new Table(3);
				table.TableFitsPage = true;
				table.BorderWidth = 1;
				table.BorderColor = new Color(0, 0, 255);
				table.Padding = 5;
				table.Spacing = 5;
				Cell cell = new Cell("header");
				cell.Header = true;
				cell.Colspan = 3;
				table.AddCell(cell);
				cell = new Cell("example cell with colspan 1 and rowspan 2");
				cell.Rowspan = 2;
				cell.BorderColor = new Color(255, 0, 0);
				table.AddCell(cell);
				table.AddCell("1.1");
				table.AddCell("2.1");
				table.AddCell("1.2");
				table.AddCell("2.2");
				table.AddCell("cell test1");
				cell = new Cell("big cell");
				cell.Rowspan = 2;
				cell.Colspan = 2;
				cell.BackgroundColor = new Color(0xC0, 0xC0, 0xC0);
				table.AddCell(cell);
				table.AddCell("cell test2");
				document.Add(new Paragraph("repeating the same table 10 times:"));
				for (int i = 0; i < 10; i++) 
				{
					document.Add(table);
				}
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
