using System;
using System.IO;
using System.Drawing;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap05
{
	/// <summary>
	/// Chap0502 ��ժҪ˵����
	/// </summary>
	public class Chap0502
	{
		public Chap0502()
		{
			Console.WriteLine("Chapter 5 example 2: adding cells at a specific position");
			// step 1: creation of a document-object
			Document document = new Document();
			try 
			{
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0502.pdf", FileMode.Create));
				// step 3: we open the document
				document.Open();
				// step 4: we create a table and add it to the document
				Table aTable;
            
				aTable = new Table(4,4);    // 4 rows, 4 columns
				aTable.AutoFillEmptyCells = true;
				aTable.AddCell("2.2", new Point(2,2));
				aTable.AddCell("3.3", new Point(3,3));
				aTable.AddCell("2.1", new Point(2,1));
				aTable.AddCell("1.3", new Point(1,3));
				document.Add(aTable);
				document.NewPage();
            
				aTable = new Table(4,4);    // 4 rows, 4 columns
				aTable.AddCell("2.2", new Point(2,2));
				aTable.AddCell("3.3", new Point(3,3));
				aTable.AddCell("2.1", new Point(2,1));
				aTable.AddCell("1.3", new Point(1,3));
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
