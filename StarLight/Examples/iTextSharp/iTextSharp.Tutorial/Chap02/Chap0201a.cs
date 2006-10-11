using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap02
{
	/// <summary>
	/// Chap0201a 的摘要说明。
	/// </summary>
	public class Chap0201a
	{
		public Chap0201a()
		{
			Console.WriteLine("Chapter 2 example 1: Horizontal space in Paragraphs");
			Document.Compress = true;
			// step 1: creation of a document-object
			Document document = new Document();



			try 
			{

				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0201a.pdf",FileMode.Create));

				// step 3: we open the document
				document.Open();

				// step 4: we add content to the document
				Font[] fonts = new Font[14];
				fonts[13] = FontFactory.GetFont(FontFactory.COURIER, 7, Font.NORMAL);
				fonts[12] = FontFactory.GetFont(FontFactory.COURIER, 14, Font.BOLD);
				fonts[11] = FontFactory.GetFont(FontFactory.COURIER, 8, Font.ITALIC);
				fonts[10] = FontFactory.GetFont(FontFactory.COURIER, 13, Font.BOLD | Font.ITALIC);
				fonts[9] = FontFactory.GetFont(FontFactory.HELVETICA, 9, Font.NORMAL);
				fonts[8] = FontFactory.GetFont(FontFactory.HELVETICA, 12, Font.BOLD);
				fonts[7] = FontFactory.GetFont(FontFactory.HELVETICA, 12, Font.ITALIC);
				fonts[6] = FontFactory.GetFont(FontFactory.HELVETICA, 11, Font.BOLD | Font.ITALIC);
				fonts[5] = FontFactory.GetFont(FontFactory.TIMES_ROMAN, 12, Font.NORMAL);
				fonts[4] = FontFactory.GetFont(FontFactory.TIMES_ROMAN, 16, Font.BOLD);
				fonts[3] = FontFactory.GetFont(FontFactory.TIMES_ROMAN, 12, Font.ITALIC);
				fonts[2] = FontFactory.GetFont(FontFactory.TIMES_ROMAN, 32, Font.BOLD | Font.ITALIC);
				fonts[1] = FontFactory.GetFont(FontFactory.SYMBOL, 5, Font.NORMAL);
				fonts[0] = FontFactory.GetFont(FontFactory.ZAPFDINGBATS, 5, Font.NORMAL);
				for (int i = 0; i < 14; i++) 
				{
					Chunk chunk = new Chunk("This is some text in some", fonts[i]);
					document.Add(new Phrase(chunk));
					document.Add(new Phrase(new Chunk(" font. ",
						fonts[i]).SetTextRise((i % 2 == 0) ? -6 : 6)));
				}
				document.Add(new Phrase(new Chunk("This text is underlined ",
					FontFactory.GetFont(FontFactory.HELVETICA, 12, Font.UNDERLINE))));
				document.Add(new Phrase(new Chunk("This font is of type ITALIC | STRIKETHRU ",
					FontFactory.GetFont(FontFactory.HELVETICA, 12, Font.ITALIC | Font.STRIKETHRU))));
				Chunk ck = new Chunk(" This text has a yellow background color ", FontFactory.GetFont(FontFactory.HELVETICA, 12));
				ck.SetBackground(new Color(0xFF, 0xFF, 0x00));
				document.Add(new Phrase(ck));

				document.Add(new Chunk("text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text"));


				Image img = Image.GetInstance("pngnow.png");
				ck = new Chunk(img, 0, 0);
				Phrase p1 = new Phrase("This is an image (not) hanging into the above text");
				p1.Add(ck);
				p1.Add(" just here.");
				document.Add(p1);

				p1 = new Phrase();
				ck = new Chunk(" As we can see the clashing is fixed for Images and"); ck.SetBackground(new Color(0xFF, 0xFF, 0x00));	p1.Add(ck);
				ck = new Chunk(" oversized font chunks", fonts[2]); ck.SetBackground(new Color(0xFF, 0xFF, 0x00));	p1.Add(ck);
				ck = new Chunk(", but no change of leading is introduced by "); ck.SetBackground(new Color(0xFF, 0xFF, 0x00));	p1.Add(ck);
				ck = new Chunk("risen ", fonts[7]);	ck.SetBackground(new Color(0xFF, 0xFF, 0x00));	p1.Add(ck);
				ck = new Chunk("(i.e. ");ck.SetBackground(new Color(0xFF, 0xFF, 0x00)); p1.Add(ck);
				ck = new Chunk("subscript").SetTextRise(-9);ck.SetBackground(new Color(0xFF, 0xFF, 0x00)); p1.Add(ck);
				ck = new Chunk(" or "); ck.SetBackground(new Color(0xFF, 0xFF, 0x00)); p1.Add(ck);
				ck = new Chunk("superscirpt").SetTextRise(9); ck.SetBackground(new Color(0xFF, 0xFF, 0x00)); p1.Add(ck);
				ck = new Chunk(") text! "); ck.SetBackground(new Color(0xFF, 0xFF, 0x00)); p1.Add(ck);
				document.Add(p1);

				document.Add(new Chunk(" text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text"));

				ck = new Chunk(img, 0, -19);
				p1 = new Phrase("This is an image (not) hanging in the subsequent text");
				p1.Add(ck);
				p1.Add(" just here.");
				document.Add(p1);

				document.Add(new Chunk("text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text"));

				ck = new Chunk(img, 0, -7);
				p1 = new Phrase("This is an image aligned to 'its' baseline ");
				p1.Add(ck);
				p1.Add(" just here.");
				document.Add(p1);

				document.Add(new Chunk("text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text text"));


			}
			catch(DocumentException de) 
			{
				Console.WriteLine(de.Message);
			}
			catch(IOException ioe) 
			{
				Console.WriteLine(ioe.Message);
			}

			// step 5: we close the document
			document.Close();
		}
	}
}
