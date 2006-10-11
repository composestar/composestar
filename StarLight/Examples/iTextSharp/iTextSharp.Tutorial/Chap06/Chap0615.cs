using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap06
{
	public class Chap0615 
	{
		public Chap0615() 
		{
			Console.WriteLine("Chapter 6 example 15: images in tables");
			// step 1: creation of a document-object
			Document document = new Document();
			try 
			{
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0615.pdf", FileMode.Create));
				// step 3: we open the document
				document.Open();
				// step 4: we create a table and add it to the document
				Image img0 = Image.GetInstance("myKids.jpg");
				img0.Alignment = Image.MIDDLE_ALIGN;
				Image img1 = Image.GetInstance("pngnow.png");
				img1.Alignment = Image.LEFT_ALIGN | Image.UNDERLYING;
				Image img2 = Image.GetInstance("pngnow.png");
				img2.Alignment = Image.RIGHT_ALIGN | Image.TEXTWRAP;
				Image img3 = Image.GetInstance("pngnow.png");
				img3.Alignment = Image.LEFT_ALIGN;
				Image img4 = Image.GetInstance("pngnow.png");
				img4.Alignment = Image.MIDDLE_ALIGN;
				Image img5 = Image.GetInstance("pngnow.png");
				img5.Alignment = Image.RIGHT_ALIGN;
				Table table = new Table(3);
				table.Padding = 2;
				table.DefaultHorizontalAlignment = Element.ALIGN_CENTER;
				// row 1
				table.AddCell("I see an image\non my right");
				Cell cell = new Cell("This is the image (aligned in the middle):");
				cell.BackgroundColor = new Color(0xC0, 0xC0, 0xC0);
				cell.Add(img0);
				cell.Add(new Phrase("This was the image"));
				table.AddCell(cell);
				table.AddCell("I see an image\non my left");
				// row 2
				cell = new Cell("This is the image (left aligned):");
				cell.Add(img1);
				cell.Add(new Phrase("This was the image"));
				table.AddCell(cell);
				table.AddCell("I see images\neverywhere");
				cell = new Cell("This is the image (right aligned):");
				cell.Add(img2);
				cell.Add(new Phrase("This was the image"));
				table.AddCell(cell);
				// row 3
				table.AddCell("I see an image\non my right");
				cell = new Cell(img3);
				cell.Add(img4);
				cell.Add(img5);
				table.AddCell(cell);
				table.AddCell("I see an image\non my left");
				document.Add(table);
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