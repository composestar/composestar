using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap11
{
	public class Chap1102 
	{
    
		public Chap1102() 
		{
        
			Console.WriteLine("Chapter 11 example 2: anchor and remote goto");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter writerA = PdfWriter.GetInstance(document, new FileStream("Chap1102a.pdf", FileMode.Create));
				PdfWriter writerB = PdfWriter.GetInstance(document, new FileStream("Chap1102b.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we add some content
            
				Paragraph p1 = new Paragraph("We discussed anchors in chapter 3, but you can add an URL to a chunk to to make it an ", FontFactory.GetFont(FontFactory.HELVETICA, 12));
				p1.Add(new Chunk("anchor", FontFactory.GetFont(FontFactory.HELVETICA, 12, Font.UNDERLINE, new Color(0, 0, 255))).SetAnchor(new Uri("http://iTextSharp.Sourceforge.Net")));
				p1.Add(" you will automatically jump to another location in this document.");
				Paragraph p2 = new Paragraph("blah, blah, blah");
				Paragraph p3a = new Paragraph("This paragraph contains a ");
				p3a.Add(new Chunk("local destination in document A", FontFactory.GetFont(FontFactory.HELVETICA, 12, Font.NORMAL, new Color(0, 255, 0))).SetLocalDestination("test"));
				Paragraph p3b = new Paragraph("This paragraph contains a local ");
				p3b.Add(new Chunk("local destination in document B", FontFactory.GetFont(FontFactory.HELVETICA, 12, Font.NORMAL, new Color(0, 255, 0))).SetLocalDestination("test"));
				Paragraph p4a = new Paragraph(new Chunk("Click this paragraph to go to a certain destination on document B").SetRemoteGoto("Chap1102b.pdf", "test"));
				Paragraph p4b = new Paragraph(new Chunk("Click this paragraph to go to a certain destination on document A").SetRemoteGoto("Chap1102a.pdf", "test"));
				Paragraph p5a = new Paragraph("you can also jump to a ");
				p5a.Add(new Chunk("specific page on another document", FontFactory.GetFont(FontFactory.HELVETICA, 12, Font.ITALIC)).SetRemoteGoto("Chap1102b.pdf", 3));
				document.Add(p1);
				document.Add(p2);
				document.Add(p2);
				document.Add(p2);
				document.Add(p2);
				document.Add(p2);
				document.Add(p2);
				document.Add(p2);
				writerA.Pause();
				document.Add(p4b);
				writerA.Resume();
				writerB.Pause();
				document.Add(p4a);
				document.Add(p5a);
				writerB.Resume();
				document.Add(p2);
				document.Add(p2);
				document.Add(p2);
				document.Add(p2);
				writerA.Pause();
				document.Add(p3b);
				document.Add(p2);
				document.Add(p2);
				document.NewPage();
				document.Add(p2);
				document.Add(p2);
				document.NewPage();
				writerA.Resume();
				writerB.Pause();
				document.Add(p3a);
				writerB.Resume();
				document.Add(p2);
				document.Add(p2);
			}
			catch(DocumentException de) 
			{
				Console.Error.WriteLine(de.Message);
			}
			catch(IOException ioe) 
			{
				Console.Error.WriteLine(ioe.Message);
			}
			catch(Exception e) 
			{
				Console.Error.WriteLine(e.Message);
			}
        
			// step 5: we close the document
			document.Close();
		}
	}
}