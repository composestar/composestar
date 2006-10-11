using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap10
{
	public class Chap1015 
	{
    
		public Chap1015() 
		{
        
			Console.WriteLine("Chapter 10 Example 15: Tiled Patterns");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1015.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we grab the ContentByte and do some stuff with it
				PdfContentByte cb = writer.DirectContent;
            
				BaseFont bf = BaseFont.CreateFont("Times-Roman", "winansi", false);
            
				// step 5: we create some PdfPatternPainter instances for drawing path, text, or placing image
            
				// Image instance to be placed in PdfPatternPainter canvas. Any nice one?
				Image img = Image.GetInstance("pngnow.png");

				PdfPatternPainter p = cb.CreatePattern(60f, 60f, 60f, 60f);
				PdfPatternPainter p1 = cb.CreatePattern(60f, 60f, 60f, 60f);
				PdfPatternPainter p2 = cb.CreatePattern(img.ScaledWidth, img.ScaledHeight, img.ScaledWidth, img.ScaledHeight);
            
            
				// step 6: put your drawing instruction in the painter canvas
            
				// A star pattern taken from Adobe PDF Reference Book p.207
				String star = "0.3 g\n15.000 27.000 m\n"
					+ "7.947 5.292 l\n26.413 18.708 l\n"
					+ "3.587 18.708 l\n22.053 5.292 l\nf\n"
					+ "45.000 57.000 m\n37.947 35.292 l\n"
					+ "56.413 48.708 l\n33.587 48.708 l\n"
					+ "52.053 35.292 l\nf\n"
					+ "0.7 g\n15.000 57.000 m\n"
					+ "7.947 35.292 l\n26.413 48.708 l\n"
					+ "3.587 48.708 l\n22.053 35.292 l\nf\n"
					+ "45.000 27.000 m\n37.947 5.292 l\n"
					+ "56.413 18.708 l\n33.587 18.708 l\n"
					+ "52.053 5.292 l\nf";
            
				p.SetLiteral(star);
            
				// A Pattern with some text drawing				
				p1.SetGrayFill(0.3f);
				p1.SetFontAndSize(bf, 12);
				p1.BeginText();
				p1.SetTextMatrix(1f, 0f, 0f, 1f, 0f, 0f);
				p1.ShowText("A B C D");
				p1.EndText();
				p1.MoveTo(0f, 0f);
				p1.LineTo(60f, 60f);
				p1.Stroke();
            
				// A pattern with an image and position
				p2.AddImage(img, img.ScaledWidth, 0f, 0f, img.ScaledHeight, 0f, 0f);
				p2.SetPatternMatrix(1f, 0f, 0f, 1f, 60f, 60f);
            
				// See if we can apply the pattern color to chunk, phrase or paragraph
				PatternColor pat = new PatternColor(p);
				PatternColor pat1 = new PatternColor(p1);
				PatternColor pat2 = new PatternColor(p2);
				String text = "Text with pattern";
				document.Add(new Paragraph(text, FontFactory.GetFont(FontFactory.HELVETICA, 60, Font.BOLD, new GrayColor(0.3f))));
				document.Add(new Paragraph(text, FontFactory.GetFont(FontFactory.HELVETICA, 60, Font.BOLD, pat)));
            
				// draw a rectangle filled with star pattern
				cb.SetPatternFill(p);
				cb.SetGrayStroke(0.0f);
				cb.Rectangle(20, 20, 284, 120);
				cb.FillStroke();
            
				// draw some characters filled with star.
				// Note: A gray, rgb, cmyk or spot color should be applied first
				// otherwise, you will not be able to see the character glyph
				// since the glyph path is filled by pattern
				cb.BeginText();
				cb.SetFontAndSize(bf, 1);
				cb.SetTextMatrix(270f, 0f, 0f, 270f, 20f, 100f);
				cb.SetGrayFill(0.9f);
				cb.ShowText("ABC");
				cb.SetPatternFill(p);
				cb.MoveTextWithLeading(0.0f, 0.0f);
				cb.ShowText("ABC");
				cb.EndText();
				cb.SetPatternFill(p);
            
				// draw a circle. Similar to rectangle
				cb.SetGrayStroke(0.0f);
				cb.Circle(150f, 400f, 150f);
				cb.FillStroke();
            
				// New Page to draw text in the pattern painter's canvas
				document.NewPage();
            
				document.Add(new Paragraph(text, FontFactory.GetFont(FontFactory.HELVETICA, 60, Font.BOLD, new GrayColor(0.3f))));
				document.Add(new Paragraph(text, FontFactory.GetFont(FontFactory.HELVETICA, 60, Font.BOLD, pat1)));
				// draw a rectangle
				cb.SetPatternFill(p1);
				cb.SetGrayStroke(0.0f);
				cb.Rectangle(0, 0, 284, 120);
				cb.FillStroke();
            
				// draw some characters
				cb.BeginText();
				cb.SetFontAndSize(bf, 1);
				cb.SetTextMatrix(270f, 0f, 0f, 270f, 20f, 100f);
				cb.SetGrayFill(0.9f);
				cb.ShowText("ABC");
				cb.SetPatternFill(p1);
				cb.MoveTextWithLeading(0.0f, 0.0f);
				cb.ShowText("ABC");
				cb.EndText();
            
				// draw a circle
				cb.SetPatternFill(p1);
				cb.SetGrayStroke(0.0f);
				cb.Circle(150f, 400f, 150f);
				cb.FillStroke();
            
				// New page to place image in the pattern painter's canvas
				document.NewPage();
				document.Add(new Paragraph(text, FontFactory.GetFont(FontFactory.HELVETICA, 60, Font.BOLD, new GrayColor(0.3f))));
				document.Add(new Paragraph(text, FontFactory.GetFont(FontFactory.HELVETICA, 60, Font.BOLD, pat2)));
				// The original Image for comparison reason.
				// Note: The width and height is the same as bbox in pattern
				cb.AddImage(img, img.ScaledWidth, 0f, 0f, img.ScaledHeight, 350f, 400f);
            
				// draw a rectangle
				cb.SetPatternFill(p2);
				cb.SetGrayStroke(0.0f);
				cb.Rectangle(60, 60, 300, 120);
				cb.FillStroke();
            
				// draw some characters.
				// Note: if the image fills up the pattern, there's no need to draw text twice
				// since colors in image will be clipped to character glyph path
				cb.BeginText();
				cb.SetFontAndSize(bf, 1);
				cb.SetTextMatrix(270f, 0f, 0f, 270f, 60f, 120f);
				cb.SetPatternFill(p2);
				cb.ShowText("ABC");
				cb.EndText();
            
				// draw a circle
				cb.SetPatternFill(p2);
				cb.SetGrayStroke(0.0f);
				cb.Circle(150f, 400f, 150f);
				cb.FillStroke();
			}
			catch(Exception e) 
			{
				Console.Error.WriteLine(e.Message);
				Console.Error.WriteLine(e.StackTrace);
			}
        
			// finally, we close the document
			document.Close();
		}
	}
}