using System;
using System.IO;
using System.Drawing;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap05
{
	/// <summary>
	/// Chap0503 的摘要说明。
	/// </summary>
	public class Chap0503
	{
		public Chap0503()
		{
			Console.WriteLine("Chapter 5 example 3: rows added automatically");
			// step 1: creation of a document-object
			Document document = new Document();
			try 
			{
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0503.pdf", FileMode.Create));
				// step 3: we open the document
				document.Open();
				// step 4: we create a table and add it to the document
				Table aTable = new Table(4,4);    // 4 rows, 4 columns
				aTable.AutoFillEmptyCells = true;
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
