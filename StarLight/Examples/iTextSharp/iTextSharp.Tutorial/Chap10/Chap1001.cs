using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap10
{
	public class Chap1001 
	{
    
		public Chap1001() 
		{
        
			Console.WriteLine("Chapter 10 example 1: Simple Graphic");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1001.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we grab the ContentByte and do some stuff with it
				PdfContentByte cb = writer.DirectContent;
            
				// an example of a rectangle with a diagonal in very thick lines
				//cb.SetLineWidth(10f;
				cb.SetLineWidth(10f);
				// draw a rectangle
				cb.Rectangle(100, 700, 100, 100);
				// add the diagonal
				cb.MoveTo(100, 700);
				cb.LineTo(200, 800);
				// stroke the lines
				cb.Stroke();
            
				// an example of some circles
				cb.SetLineDash(3, 3, 0);
				cb.SetRGBColorStrokeF(0f, 255f, 0f);
				cb.Circle(150f, 500f, 100f);
				cb.Stroke();
            
				cb.SetLineWidth(5f);
				cb.ResetRGBColorStroke();
				cb.Circle(150f, 500f, 50f);
				cb.Stroke();
            
				// example with colorfill
				cb.SetRGBColorFillF(0f, 255f, 0f);
				cb.MoveTo(100f, 200f);
				cb.LineTo(200f, 250f);
				cb.LineTo(400f, 150f);
				// because we change the fill color BEFORE we stroke the triangle
				// the color of the triangle will be red instead of green
				cb.SetRGBColorFillF(255f, 0f, 0f);
				cb.ClosePathFillStroke();
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