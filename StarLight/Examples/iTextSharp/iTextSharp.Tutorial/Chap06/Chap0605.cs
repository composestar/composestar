using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap06
{
	public class Chap0605 
	{
		public Chap0605() 
		{
			Console.WriteLine("Chapter 6 example 5: Alignment of images");
			// step 1: creation of a document-object
			Document document = new Document();
			try 
			{
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0605.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				Image gif = Image.GetInstance("vonnegut.gif");
				gif.Alignment = Image.RIGHT_ALIGN | Image.TEXTWRAP;
				Image jpeg = Image.GetInstance("myKids.jpg");
				jpeg.Alignment = Image.MIDDLE_ALIGN;
				Image png = Image.GetInstance("hitchcock.png");
				png.Alignment = Image.LEFT_ALIGN | Image.UNDERLYING;
            
				for (int i = 0; i < 100; i++) 
				{
					document.Add(new Phrase("Who is this? "));
				}
				document.Add(gif);
				for (int i = 0; i < 100; i++) 
				{
					document.Add(new Phrase("Who is this? "));
				}
				document.Add(jpeg);
				for (int i = 0; i < 100; i++) 
				{
					document.Add(new Phrase("Who is this? "));
				}
				document.Add(png);
				for (int i = 0; i < 100; i++) 
				{
					document.Add(new Phrase("Who is this? "));
				}
				document.Add(gif);
				for (int i = 0; i < 100; i++) 
				{
					document.Add(new Phrase("Who is this? "));
				}
				document.Add(jpeg);
				for (int i = 0; i < 100; i++) 
				{
					document.Add(new Phrase("Who is this? "));
				}
				document.Add(png);
				for (int i = 0; i < 100; i++) 
				{
					document.Add(new Phrase("Who is this? "));
				}
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