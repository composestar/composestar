using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap04
{
	/// <summary>
	/// Chap0403 的摘要说明。
	/// </summary>
	public class Chap0403
	{
		public Chap0403()
		{
			Console.WriteLine("Chapter 4 example 3: Chapters and Sections");
        
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			try 
			{
				// step 2: we create a writer that listens to the document
				PdfWriter writer=PdfWriter.GetInstance(document, new FileStream("Chap0403.pdf", FileMode.Create));
				// step 3: we open the document
				document.Open();
				// step 4: we Add content to the document
				Paragraph title1 = new Paragraph("This is Chapter 1", FontFactory.GetFont(FontFactory.HELVETICA, 18, Font.BOLDITALIC, new Color(0, 0, 255)));
				Chapter chapter1 = new Chapter(title1, 2);
				chapter1.NumberDepth = 0;
				Paragraph someText = new Paragraph("This is some text");
				chapter1.Add(someText);
				Paragraph title11 = new Paragraph("This is Section 1 in Chapter 1", FontFactory.GetFont(FontFactory.HELVETICA, 16, Font.BOLD, new Color(255, 0, 0)));
				Section section1 = chapter1.AddSection(title11);
				Paragraph someSectionText = new Paragraph("This is some silly paragraph in a chapter and/or section. It contains some text to test the functionality of Chapters and Section.");
				section1.Add(someSectionText);
				document.Add(chapter1);
            
				Paragraph title2 = new Paragraph("This is Chapter 2", FontFactory.GetFont(FontFactory.HELVETICA, 18, Font.BOLDITALIC, new Color(0, 0, 255)));
				Chapter chapter2 = new Chapter(title2, 2);
				chapter2.NumberDepth = 0;
				chapter2.Add(someText);
				Paragraph title21 = new Paragraph("This is Section 1 in Chapter 2", FontFactory.GetFont(FontFactory.HELVETICA, 16, Font.BOLD, new Color(255, 0, 0)));
				Section section2 = chapter2.AddSection(title21);
				section2.Add(someSectionText);
				chapter2.BookmarkOpen = false;
				document.Add(chapter2);
			}
			catch(Exception de) 
			{
				Console.Error.WriteLine(de.StackTrace);
			}
			// step 5: we close the document
			document.Close();

		}
	}
}
