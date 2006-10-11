using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap04
{
	/// <summary>
	/// Chap0405 的摘要说明。
	/// </summary>
	public class Chap0405
	{
		public Chap0405()
		{
			Console.WriteLine("Chapter 4 example 5: page borders and horizontal lines");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2: we create a writer that listens to the document
				PdfWriter.GetInstance(document, new FileStream("Chap0405.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we Add a paragraph to the document
				Graphic g = new Graphic();
				g.SetBorder(3f, 5f);
				document.Add(g);
				document.Add(new Paragraph("Hello World"));
				document.Add(new Paragraph("Hello World\n\n"));
				g = new Graphic();
				g.SetHorizontalLine(5f, 100f);
				document.Add(g);
				document.Add(new Paragraph("Hello World"));
				document.Add(new Paragraph("Hello World\n\n"));
				g = new Graphic();
				g.SetHorizontalLine(2f, 80f, new Color(0xFF, 0x00, 0x00));
				document.Add(g);
				document.Add(new Paragraph("Hello World"));
            
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
