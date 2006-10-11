using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap01
{
	/// <summary>
	/// Chapter 1 example 2: PageSize
	/// </summary>
	public class Chap0102
	{
		public Chap0102()
		{
			Console.WriteLine("Chapter 1 example 2: PageSize");

			// step 1: creation of a document-object
			Rectangle pageSize = new Rectangle(144, 720);
			pageSize.BackgroundColor = new Color(0xFF, 0xFF, 0xDE);
			Document document = new Document(pageSize);
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
            
				PdfWriter.GetInstance(document, new FileStream("Chap0102.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we Add some paragraphs to the document
				for (int i = 0; i < 5; i++) 
				{
					document.Add(new Paragraph("Hello World"));
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
