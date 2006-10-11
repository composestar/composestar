using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap10
{
	public class Chap1003 
	{
    
		public Chap1003() 
		{
        
			Console.WriteLine("Chapter 10 example 3: Templates");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1003.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we grab the ContentByte and do some stuff with it
				PdfContentByte cb = writer.DirectContent;
            
				// we create a PdfTemplate
				PdfTemplate template = cb.CreateTemplate(500, 200);
            
				// we add some graphics
				template.MoveTo(0, 200);
				template.LineTo(500, 0);
				template.Stroke();
				template.SetRGBColorStrokeF(255f, 0f, 0f);
				template.Circle(250f, 100f, 80f);
				template.Stroke();
            
				// we add some text
				template.BeginText();
				BaseFont bf = BaseFont.CreateFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				template.SetFontAndSize(bf, 12);
				template.SetTextMatrix(100, 100);
				template.ShowText("Text at the position 100,100 (relative to the template!)");
				template.EndText();
            
				// we add the template on different positions
				cb.AddTemplate(template, 0, 0);
				cb.AddTemplate(template, 0, 1, -1, 0, 500, 200);
				cb.AddTemplate(template, .5f, 0, 0, .5f, 100, 400);
            
				// we go to a new page
				document.NewPage();
				cb.AddTemplate(template, 0, 400);
				cb.AddTemplate(template, 2, 0, 0, 2, -200, 400);
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