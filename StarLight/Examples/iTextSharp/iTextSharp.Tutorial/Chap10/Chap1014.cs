using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap10
{
	public class Chap1014 
	{
    
    
		public Chap1014() 
		{
        
			Console.WriteLine("Chapter 10 Example 14: colored patterns");
        
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			Document.Compress = false;
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1014.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we add some content
				PdfContentByte cb = writer.DirectContent;
				PdfTemplate tp = cb.CreateTemplate(400, 300);
				PdfPatternPainter pat = cb.CreatePattern(15, 15, null);
				pat.Rectangle(5, 5, 5, 5);
				pat.Fill();
				PdfSpotColor spc_cmyk = new PdfSpotColor("PANTONE 280 CV", 0.25f, new CMYKColor(0.9f, .2f, .3f, .1f));
				SpotColor spot = new SpotColor(spc_cmyk);
				tp.SetPatternFill(pat, spot, .9f);
				tp.Rectangle(0, 0, 400, 300);
				tp.Fill();
				cb.AddTemplate(tp, 50, 50);
				PdfPatternPainter pat2 = cb.CreatePattern(10, 10, null);
				pat2.SetLineWidth(2);
				pat2.MoveTo(-5, 0);
				pat2.LineTo(10, 15);
				pat2.Stroke();
				pat2.MoveTo(0, -5);
				pat2.LineTo(15, 10);
				pat2.Stroke();
				cb.SetLineWidth(1);
				cb.SetColorStroke( new Color(System.Drawing.Color.Black));
				cb.SetPatternFill(pat2, new Color(System.Drawing.Color.Red));
				cb.Rectangle(100, 400, 30, 210);
				cb.FillStroke();
				cb.SetPatternFill(pat2, new Color(System.Drawing.Color.LightGreen));
				cb.Rectangle(150, 400, 30, 100);
				cb.FillStroke();
				cb.SetPatternFill(pat2, new Color(System.Drawing.Color.Blue));
				cb.Rectangle(200, 400, 30, 130);
				cb.FillStroke();
				cb.SetPatternFill(pat2, new GrayColor(0.5f));
				cb.Rectangle(250, 400, 30, 80);
				cb.FillStroke();
				cb.SetPatternFill(pat2, new GrayColor(0.7f));
				cb.Rectangle(300, 400, 30, 170);
				cb.FillStroke();
				cb.SetPatternFill(pat2, new GrayColor(0.9f));
				cb.Rectangle(350, 400, 30, 40);
				cb.FillStroke();
			}
			catch (Exception de) 
			{
				Console.Error.WriteLine(de.Message);
				Console.Error.WriteLine(de.StackTrace);
			}
			// step 5: we close the document
			document.Close();
		}
	}
}