using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap04
{
	/// <summary>
	/// Chap0404 的摘要说明。
	/// </summary>
	public class Chap0404
	{
		public Chap0404()
		{
			Console.WriteLine("Chapter 4 example 4: Simple Graphic");

			// step 1: creation of a document-object
			Document document = new Document();

			try 
			{

				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file

				PdfWriter.GetInstance(document, new FileStream("Chap0404.pdf", FileMode.Create));

				// step 3: we open the document
				document.Open();

				// step 4: we add a Graphic to the document
				Graphic grx = new Graphic();
				// add a rectangle
				grx.Rectangle(100, 700, 100, 100);
				// add the diagonal
				grx.MoveTo(100, 700);
				grx.LineTo(200, 800);
				// stroke the lines
				grx.Stroke();
				document.Add(grx);

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
