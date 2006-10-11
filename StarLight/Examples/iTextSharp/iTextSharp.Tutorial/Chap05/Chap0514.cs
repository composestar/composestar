using System;
using System.Drawing;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap05
{
	/// <summary>
	/// Chap0514 的摘要说明。
	/// </summary>
	public class Chap0514
	{
		public Chap0514()
		{
			Console.WriteLine("Chapter 5 example 14: nested tables");
			// step 1: creation of a document-object
			Document document = new Document();
			try 
			{
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0514.pdf", FileMode.Create));
				// step 3: we open the document
				document.Open();
				// step 4: we create a table and add it to the document
            
				// simple example
            
				Table secondTable = new Table(2);
				secondTable.AddCell("2nd table 0.0");
				secondTable.AddCell("2nd table 0.1");
				secondTable.AddCell("2nd table 1.0");
				secondTable.AddCell("2nd table 1.1");
            
				Table aTable = new Table(4,4);    // 4 rows, 4 columns
				aTable.AutoFillEmptyCells = true;
				aTable.AddCell("2.2", new Point(2,2));
				aTable.AddCell("3.3", new Point(3,3));
				aTable.AddCell("2.1", new Point(2,1));
				aTable.InsertTable(secondTable, new Point(1,3));
				document.Add(aTable);
            
				// example with 2 nested tables
            
				Table thirdTable = new Table(2);
				thirdTable.AddCell("3rd table 0.0");
				thirdTable.AddCell("3rd table 0.1");
				thirdTable.AddCell("3rd table 1.0");
				thirdTable.AddCell("3rd table 1.1");
            
				aTable = new Table(5,5);
				aTable.AutoFillEmptyCells = true;
				aTable.AddCell("2.2", new Point(2,2));
				aTable.AddCell("3.3", new Point(3,3));
				aTable.AddCell("2.1", new Point(2,1));
				aTable.InsertTable(secondTable, new Point(1,3));
				aTable.InsertTable(thirdTable, new Point(6,2));
				document.Add(aTable);        
            
				// relative column widths are preserved
            
				Table a = new Table( 2 );
				a.Widths = new float[] { 85, 15 };
				a.AddCell("a-1");
				a.AddCell("a-2");
            
				Table b = new Table(5);
				b.Widths = new float[] { 15, 7, 7, 7, 7 };
				b.AddCell("b-1");
				b.AddCell("b-2");
				b.AddCell("b-3");
				b.AddCell("b-4");
				b.AddCell("b-5");
            
				// now, insert these 2 tables into a third for layout purposes
				Table c = new Table( 3, 1 );
				c.WidthPercentage = 100.0f;
				c.Widths = new float[] { 20, 2, 78 };
				c.InsertTable(a, new Point(0,0) );
				c.InsertTable(b, new Point(0,2) );

				document.Add(c);
            
				// adding extra cells after adding a table
            
				Table t1 = new Table(3);
				t1.AddCell("1.1");
				t1.AddCell("1.2");
				t1.AddCell("1.3");
				// nested
				Table t2 = new Table(2);
				t2.AddCell("2.1");
				t2.AddCell("2.2");
            
				// now insert the nested
				t1.InsertTable(t2);
				t1.AddCell("new cell");    // correct row/column ?
				document.Add(t1);       
            
				// deep nesting
            
				t1=new Table(2,2); 
				for (int i = 0; i < 4; i++) 
				{
					t1.AddCell("t1");
				}
            
				t2=new Table(3,3);
				for (int i = 0; i < 9; i++) 
				{
					if (i == 4) t2.InsertTable(t1);
					else t2.AddCell("t2"); 
				}
            
				Table t3=new Table(4,4);
				for (int i = 0; i < 16; i++) 
				{
					if (i == 10) t3.InsertTable(t2);
					else t3.AddCell("t3"); 
				}

				document.Add(t3); 
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
