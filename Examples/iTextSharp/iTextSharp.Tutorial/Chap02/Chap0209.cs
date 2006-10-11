using System;
using System.IO;
using System.Collections;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap02
{
	class Glossary : PdfPageEventHelper 
	{
    
		// we will keep a glossary of words and the pages they appear on in a TreeMap
		SortedList glossary = new SortedList();
    
		// we override only the onGenericTag method
		public override void OnGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) 
		{
			glossary.Add(text, writer.PageNumber);
		}
    
		// we Add a method to retrieve the glossary
		public SortedList GetGlossary() 
		{
			return glossary;
		}
    
	}

	/// <summary>
	/// Chap0209 的摘要说明。
	/// </summary>
	public class Chap0209
	{
		public Chap0209()
		{
			Console.WriteLine("Chapter 2 example 9: generic tags");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap0209.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4:
				// we create an Event and Add it to the writer
				Glossary pageEvent = new Glossary();
				writer.PageEvent = pageEvent;
            
				// we Add some content
				String[] f = new String[14];
				f[0] = "Courier";
				f[1] = "Courier Bold";
				f[2] = "Courier Italic";
				f[3] = "Courier Bold Italic";
				f[4] = "Helvetica";
				f[5] = "Helvetica bold";
				f[6] = "Helvetica italic";
				f[7] = "Helvetica bold italic";
				f[8] = "Times New Roman";
				f[9] = "Times New Roman bold";
				f[10] = "Times New Roman italic";
				f[11] = "Times New Roman bold italic";
				f[12] = "Symbol";
				f[13] = "Zapfdingbats";
				Font[] fonts = new Font[14];
				fonts[0] = FontFactory.GetFont(FontFactory.COURIER, 12, Font.NORMAL);
				fonts[1] = FontFactory.GetFont(FontFactory.COURIER, 12, Font.BOLD);
				fonts[2] = FontFactory.GetFont(FontFactory.COURIER, 12, Font.ITALIC);
				fonts[3] = FontFactory.GetFont(FontFactory.COURIER, 12, Font.BOLD | Font.ITALIC);
				fonts[4] = FontFactory.GetFont(FontFactory.HELVETICA, 12, Font.NORMAL);
				fonts[5] = FontFactory.GetFont(FontFactory.HELVETICA, 12, Font.BOLD);
				fonts[6] = FontFactory.GetFont(FontFactory.HELVETICA, 12, Font.ITALIC);
				fonts[7] = FontFactory.GetFont(FontFactory.HELVETICA, 12, Font.BOLD | Font.ITALIC);
				fonts[8] = FontFactory.GetFont(FontFactory.TIMES_ROMAN, 12, Font.NORMAL);
				fonts[9] = FontFactory.GetFont(FontFactory.TIMES_ROMAN, 12, Font.BOLD);
				fonts[10] = FontFactory.GetFont(FontFactory.TIMES_ROMAN, 12, Font.ITALIC);
				fonts[11] = FontFactory.GetFont(FontFactory.TIMES_ROMAN, 12, Font.BOLD | Font.ITALIC);
				fonts[12] = FontFactory.GetFont(FontFactory.SYMBOL, 12, Font.NORMAL);
				fonts[13] = FontFactory.GetFont(FontFactory.ZAPFDINGBATS, 12, Font.NORMAL);
				for (int i = 0; i < 14; i++) 
				{
					Chunk chunk = new Chunk("This is font ", fonts[i]);
					Paragraph p = new Paragraph(chunk);
					p.Add(new Phrase(new Chunk(f[i], fonts[i]).SetGenericTag(f[i])));
					document.Add(p);
					if (i % 4 == 3) 
					{
						document.NewPage();
					}
				}
            
				// we Add the glossary
				document.NewPage();
				SortedList glossary = pageEvent.GetGlossary();
				foreach(string key in glossary.Keys) 
				{
					int page = (int)glossary[key];
					Paragraph g = new Paragraph(key);
					g.Add(" : page ");
					g.Add(page.ToString());
					document.Add(g);
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
