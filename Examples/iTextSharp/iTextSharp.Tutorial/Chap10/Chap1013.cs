using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap10
{
	public class Chap1013 
	{
    
		public Chap1013() 
		{
        
			Console.WriteLine("Chapter 10 Example 13: Spot Color");
        
			// step 1: creation of a document-object
			Document document = new Document();
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1013.pdf", FileMode.Create));
				BaseFont bf = BaseFont.CreateFont("Helvetica", "winansi", BaseFont.NOT_EMBEDDED);
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we add some content
				PdfContentByte cb = writer.DirectContent;
            
				// Note: I made up these names unless someone give me a PANTONE swatch as gift (phillip@formstar.Com)
				PdfSpotColor spc_cmyk = new PdfSpotColor("PANTONE 280 CV", 0.25f, new CMYKColor(0.9f, .2f, .3f, .1f));
				PdfSpotColor spc_rgb = new PdfSpotColor("PANTONE 147", 0.9f, new Color(114, 94, 38));
				PdfSpotColor spc_g = new PdfSpotColor("PANTONE 100 CV", 0.5f, new GrayColor(0.9f));
            
				// Stroke a rectangle with CMYK alternate
				cb.SetColorStroke(spc_cmyk, .5f);
				cb.SetLineWidth(10f);
				// draw a rectangle
				cb.Rectangle(100, 700, 100, 100);
				// add the diagonal
				cb.MoveTo(100, 700);
				cb.LineTo(200, 800);
				// stroke the lines
				cb.Stroke();
            
				// Fill a rectangle with CMYK alternate
				cb.SetColorFill(spc_cmyk, spc_cmyk.Tint);
				cb.Rectangle(250, 700, 100, 100);
				cb.Fill();
            
				// Stroke a circle with RGB alternate
				cb.SetColorStroke(spc_rgb, spc_rgb.Tint);
				cb.SetLineWidth(5f);
				cb.Circle(150f, 500f, 100f);
				cb.Stroke();
            
				// Fill the circle with RGB alternate
				cb.SetColorFill(spc_rgb, spc_rgb.Tint);
				cb.Circle(150f, 500f, 50f);
				cb.Fill();
            
				// example with colorfill
				cb.SetColorFill(spc_g, spc_g.Tint);
				cb.MoveTo(100f, 200f);
				cb.LineTo(200f, 250f);
				cb.LineTo(400f, 150f);
				cb.Fill();
				document.NewPage();
				String text = "Some text to show";
				document.Add(new Paragraph(text, FontFactory.GetFont(FontFactory.HELVETICA, 24, Font.NORMAL, new SpotColor(spc_cmyk))));
				document.Add(new Paragraph(text, FontFactory.GetFont(FontFactory.HELVETICA, 24, Font.NORMAL, new SpotColor(spc_cmyk, 0.5f))));
            
				// example with template
				PdfTemplate t = cb.CreateTemplate(500f, 500f);
				// Stroke a rectangle with CMYK alternate
				t.SetColorStroke(new SpotColor(spc_cmyk, .5f));
				t.SetLineWidth(10f);
				// draw a rectangle
				t.Rectangle(100, 10, 100, 100);
				// add the diagonal
				t.MoveTo(100, 10);
				t.LineTo(200, 100);
				// stroke the lines
				t.Stroke();
            
				// Fill a rectangle with CMYK alternate
				t.SetColorFill(spc_g, spc_g.Tint);
				t.Rectangle(100, 125, 100, 100);
				t.Fill();
				t.BeginText();
				t.SetFontAndSize(bf, 20f);
				t.SetTextMatrix(1f, 0f, 0f, 1f, 10f, 10f);
				t.ShowText("Template text upside down");
				t.EndText();
				t.Rectangle(0, 0, 499, 499);
				t.Stroke();
				cb.AddTemplate(t, -1.0f, 0.00f, 0.00f, -1.0f, 550f, 550f);
			}
			catch(Exception de) 
			{
				Console.Error.WriteLine(de.Message);
				Console.Error.WriteLine(de.StackTrace);
			}
        
			// step 5: we close the document
			document.Close();
		}
	}
}