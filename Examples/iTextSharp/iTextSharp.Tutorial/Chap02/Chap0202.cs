using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap02
{
	/// <summary>
	/// Chap0202 的摘要说明。
	/// </summary>
	public class Chap0202
	{
		public Chap0202()
		{
			Console.WriteLine("Chapter 2 example 2: Phrases");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0202.pdf",FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we Add a paragraph to the document
				Phrase phrase0 = new Phrase();
				Phrase phrase1 = new Phrase("(1) this is a phrase\n");
				// In this example the leading is passed as a parameter
				Phrase phrase2 = new Phrase(24, "(2) this is a phrase with leading 24. You can only see the difference if the line is long enough. Do you see it? There is more space between this line and the previous one.\n");
				// When a Font is passed (explicitely or embedded in a chunk),
				// the default leading = 1.5 * size of the font
				Phrase phrase3 = new Phrase("(3) this is a phrase with a red, normal font Courier, size 20. As you can see the leading is automatically changed.\n", FontFactory.GetFont(FontFactory.COURIER, 20, Font.NORMAL, new Color(255, 0, 0)));
				Phrase phrase4 = new Phrase(new Chunk("(4) this is a phrase\n"));
				Phrase phrase5 = new Phrase(18, new Chunk("(5) this is a phrase in Helvetica, bold, red and size 16 with a given leading of 18 points.\n", FontFactory.GetFont(FontFactory.HELVETICA, 16, Font.BOLD, new Color(255, 0, 0))));
				// A Phrase can contains several chunks with different fonts
				Phrase phrase6 = new Phrase("(6)");
				Chunk chunk = new Chunk(" This is a font: ");
				phrase6.Add(chunk);
				phrase6.Add(new Chunk("Helvetica", FontFactory.GetFont(FontFactory.HELVETICA, 12)));
				phrase6.Add(chunk);
				phrase6.Add(new Chunk("Times New Roman", FontFactory.GetFont(FontFactory.TIMES_ROMAN, 12)));
				phrase6.Add(chunk);
				phrase6.Add(new Chunk("Courier", FontFactory.GetFont(FontFactory.COURIER, 12)));
				phrase6.Add(chunk);
				phrase6.Add(new Chunk("Symbol", FontFactory.GetFont(FontFactory.SYMBOL, 12)));
				phrase6.Add(chunk);
				phrase6.Add(new Chunk("ZapfDingBats", FontFactory.GetFont(FontFactory.ZAPFDINGBATS, 12)));
				Phrase phrase7 = new Phrase("(7) if you don't Add a newline yourself, all phrases are glued to eachother!");
            
				document.Add(phrase1);
				document.Add(phrase2);
				document.Add(phrase3);
				document.Add(phrase4);
				document.Add(phrase5);
				document.Add(phrase6);
				document.Add(phrase7);
            
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
