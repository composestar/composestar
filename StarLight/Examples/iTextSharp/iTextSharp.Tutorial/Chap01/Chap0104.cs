using System;
using System.IO;
using System.Diagnostics;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap01
{
	/// <summary>
	/// Chap0104 的摘要说明。
	/// </summary>
	public class Chap0104
	{
		public Chap0104()
		{
			Console.WriteLine("Chapter 1 example 4: Margins");
        
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A5, 36, 72, 108, 180);
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
            
				PdfWriter.GetInstance(document, new FileStream("Chap0104.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we Add a paragraph to the document
				Paragraph paragraph = new Paragraph();
				paragraph.Alignment = Element.ALIGN_JUSTIFIED;
				for (int i = 0; i < 20; i++) 
				{
					paragraph.Add("Hello World, Hello Sun, Hello Moon, Hello Stars, Hello Sea, Hello Land, Hello People. ");
				}
				document.Add(paragraph);
            
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
