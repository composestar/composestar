using System;
using System.IO;
using System.Drawing;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap05
{
	/// <summary>
	/// Chap0504 的摘要说明。
	/// </summary>
	public class Chap0504
	{
		public Chap0504()
		{
			Console.WriteLine("Chapter 5 example 4: adding columns");
			// step 1: creation of a document-object
			Document document = new Document();
			try 
			{
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0504.pdf", FileMode.Create));
				// step 3: we open the document
				document.Open();
				// step 4: we create a table and add it to the document
				Table aTable = new Table(2,2);    // 2 rows, 2 columns
				aTable.AutoFillEmptyCells = true;
				aTable.AddCell("0.0");
				aTable.AddCell("0.1");
				aTable.AddCell("1.0");
				aTable.AddCell("1.1");
				aTable.AddColumns(2);
				float[] f = {1f, 1f, 1f, 1f};
				aTable.Widths = f;
				aTable.AddCell("2.2", new Point(2,2));
				aTable.AddCell("3.3", new Point(3,3));
				aTable.AddCell("2.1", new Point(2,1));
				aTable.AddCell("1.3", new Point(1,3));
				aTable.AddCell("5.2", new Point(5,2));
				aTable.AddCell("6.1", new Point(6,1));
				aTable.AddCell("5.0", new Point(5,0)); 
				document.Add(aTable);  
            
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
