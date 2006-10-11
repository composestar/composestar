using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap02
{
	/// <summary>
	/// Chap0205 的摘要说明。
	/// </summary>
	public class Chap0205
	{
		public Chap0205()
		{
			Console.WriteLine("Chapter 2 example 5: Paragraphs");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0205.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we Add a paragraph to the document
				Paragraph p1 = new Paragraph(new Chunk("This is my first paragraph. ",
					FontFactory.GetFont(FontFactory.HELVETICA, 10)));
				p1.Add("The leading of this paragraph is calculated automagically. ");
				p1.Add("The default leading is 1.5 times the fontsize. ");
				p1.Add(new Chunk("You can Add chunks "));
				p1.Add(new Phrase("or you can Add phrases. "));
				p1.Add(new Phrase("Unless you change the leading with the method setLeading, the leading doesn't change if you Add text with another leading. This can lead to some problems.", FontFactory.GetFont(FontFactory.HELVETICA, 18)));
				document.Add(p1);
				Paragraph p2 = new Paragraph(new Phrase("This is my second paragraph. ",
					FontFactory.GetFont(FontFactory.HELVETICA, 12)));
				p2.Add("As you can see, it started on a new line.");
				document.Add(p2);
				Paragraph p3 = new Paragraph("This is my third paragraph.",
					FontFactory.GetFont(FontFactory.HELVETICA, 12));
				document.Add(p3);
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
