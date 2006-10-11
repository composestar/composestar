using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap10
{
	public class Chap1012 
	{
		static public String[] headings = {
											  "Book/Product Model:",
											  "Sales Handle:",
											  "Why We Published this Book/Product Model:",
											  "Key benefits:",
											  "About the Author(s):",
											  "Technology/Topic Overview: ",
											  "Book/Product Content Summary:", 
											  "Audience:",
											  "What's on the CD/DVD/Web:"
										  };

		static public String[] texts = {
										   "Ideally, choose one title (2-3 if absolutely necessary) that this book should perform like. Include full title, ISBN, author, and any sell through numbers if possible.",
										   "One line description about the sales.",
										   "Brief description (one-two lines) on the importance of this book to the audience.",
										   "What benefit does this book provide to the consumer? (expert advice, speed, fun, productivity). Why should the Retailer/Wholesaler select this book over its competition? What are the unique features about this book should be highlighted? What makes this book different, better? From other books and the previous edition?",
										   "What makes this person so special?  Is she/he an expert, creator of the technology, educational leader, etc.? What is their background, and what relevant experiences do they have to make them the BEST choice? Have he/she/they won awards or been recognized in any way. Other books poublished by the author.\n1. Book one.\n2. Book two.",
										   "In brief two to five line description of the technology, topic or relevant information. Please keep descriptions succinct.",
										   "Ideal describe the contents of this book. What will this book do for the reader? Will this book help them optimize their system? Increase productivity? offer tips and stragegies?",
										   "Who is your intended customer? Experts? Power users? Business professionals? Programmers? What are the demographics?",
										   "What is included on the Cd or Web site? Why is it necessary and what will it do for the purchaser (source code, examples, case studies)?\nIs there a value that can be associated with what is on the CD/DVD or Web?"
									   };

		public Chap1012() 
		{
        
			Console.WriteLine("Chapter 10 example 12: PdfPTables and columns");
            
			// step 1: creation of a document-object
			Document document = new Document(PageSize.LETTER, 90, 54, 72, 72);
			try 
			{
				// step 2: we create a writer that listens to the document
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1012.pdf", FileMode.Create));
            
				float gutter = 20;
				int numColumns = 3;
				float fullWidth = document.Right - document.Left;
				float columnWidth = (fullWidth - (numColumns - 1) * gutter) / numColumns;
				float[] allColumns = new float[numColumns]; // left
				for (int k = 0; k < numColumns; ++k) 
				{
					allColumns[k] = document.Left + (columnWidth + gutter) * k;
				}
				// set the fonts
				Font font24B = FontFactory.GetFont(FontFactory.TIMES_ROMAN, 24, Font.BOLD);
				Font font10B = FontFactory.GetFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD);
				Font font14B = FontFactory.GetFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, new Color(255, 0, 0));
				Font font9 = FontFactory.GetFont(FontFactory.TIMES_ROMAN, 9);
				Font font11 = FontFactory.GetFont(FontFactory.TIMES_ROMAN, 11);
            
				// step 3: we open the document
				document.Open();            
				// step 4:
				// get the stream content
				PdfContentByte cb = writer.DirectContent;
				// headers
				Phrase fullTitle = new Phrase("Full Title", font24B);
				float currentY = document.Top;
				ColumnText ct = new ColumnText(cb);
				ct.SetSimpleColumn(fullTitle, document.Left, 0, document.Right, document.Top, 24, Element.ALIGN_JUSTIFIED);
				ct.Go();
				currentY = ct.YLine;
				currentY -= 4;
				cb.SetLineWidth(1);
				cb.MoveTo(document.Left, currentY);
				cb.LineTo(document.Right, currentY);
				
				cb.Stroke();
				currentY -= 4;
				ct.YLine = currentY;
				ct.AddText(new Chunk("Author: Name of the author comes here", font10B));
				ct.Leading = 10;
				ct.Go();
				currentY = ct.YLine;
				currentY -= 15;
				float topColumn = currentY;
				for (int k = 1; k < numColumns; ++k) 
				{
					float x = allColumns[k] - gutter / 2;
					cb.MoveTo(x, topColumn);
					cb.LineTo(x, document.Bottom);
				}
				cb.Stroke();
				Image img = Image.GetInstance("cover.png");
				cb.AddImage(img, img.ScaledWidth, 0, 0, img.ScaledHeight, document.Left, currentY - img.ScaledHeight);
				currentY -= img.ScaledHeight + 10;
				ct.YLine = currentY;
				ct.AddText(new Chunk("Key Data:", font14B));
				ct.Go();
				currentY = ct.YLine;
				currentY -= 4;
				PdfPTable ptable = new PdfPTable(2);
				ptable.DefaultCell.PaddingLeft = 4;
				ptable.DefaultCell.PaddingTop = 0;
				ptable.DefaultCell.PaddingBottom = 4;
				ptable.AddCell(new Phrase("Imprint Name:", font9));
				ptable.AddCell(new Phrase("Prentice Hall", font9));
				ptable.AddCell(new Phrase("Series Name:", font9));
				ptable.AddCell(new Phrase("", font9));
				ptable.AddCell(new Phrase("ISBN:", font9));
				ptable.AddCell(new Phrase("Hall", font9));
				ptable.AddCell(new Phrase("UPC Code:", font9));
				ptable.AddCell(new Phrase("0789718103", font9));
				ptable.AddCell(new Phrase("EAN #", font9));
				ptable.AddCell(new Phrase("0786718103", font9));
				ptable.AddCell(new Phrase("Price:", font9));
				ptable.AddCell(new Phrase("49.99", font9));
				ptable.AddCell(new Phrase("Page Count:", font9));
				ptable.AddCell(new Phrase("500", font9));
				ptable.AddCell(new Phrase("Discount:", font9));
				ptable.AddCell(new Phrase("10%", font9));
				ptable.AddCell(new Phrase("Trim Size:", font9));
				ptable.AddCell(new Phrase("420x340", font9));
				ptable.AddCell(new Phrase("Cover:", font9));
				ptable.AddCell(new Phrase("Hard", font9));
				ptable.AddCell(new Phrase("Interior Color:", font9));
				ptable.AddCell(new Phrase("none", font9));
				ptable.AddCell(new Phrase("Media with book:", font9));
				ptable.AddCell(new Phrase("CD", font9));
				ptable.AddCell(new Phrase("Author(s):", font9));
				ptable.AddCell(new Phrase("Ben Forta", font9));
				ptable.AddCell(new Phrase("Editor:", font9));
				ptable.AddCell(new Phrase("Ben Forta", font9));
				ptable.AddCell(new Phrase("Pub Date:", font9));
				ptable.AddCell(new Phrase("06/05/1998", font9));
				ptable.TotalWidth = columnWidth;

				currentY = ptable.WriteSelectedRows(0, -1, document.Left, currentY, cb) - 20;
				for (int k = 0; k < headings.Length; ++k) 
				{
					ct.AddText(new Chunk(headings[k] + "\n", font14B));
					ct.AddText(new Chunk(texts[k] + "\n\n", font11));
				}

				int currentColumn = 0;
				ct.SetSimpleColumn(allColumns[currentColumn], document.Bottom,
					allColumns[currentColumn] + columnWidth, currentY, 15, Element.ALIGN_JUSTIFIED);
				ct.SetLeading(2, 1);
				for (;;) 
				{
					int rc = ct.Go();
					if ((rc & ColumnText.NO_MORE_TEXT) != 0)
						break;
					// we run out of column. Let's go to another one
					++currentColumn;
					if (currentColumn >= allColumns.Length)
						break;
					ct.SetSimpleColumn(allColumns[currentColumn], document.Bottom,
						allColumns[currentColumn] + columnWidth, topColumn, 15, Element.ALIGN_JUSTIFIED);
					ct.SetLeading(2, 1);
				}
				// step 5: we close the document
				document.Close();
			}
			catch (Exception de) 
			{
				Console.Error.WriteLine(de.Message);
				Console.Error.WriteLine(de.StackTrace);
			}
		}
	}
}