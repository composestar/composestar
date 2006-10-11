using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap10
{
	public class Chap1002 
	{
    
		public Chap1002() 
		{
        
			Console.WriteLine("Chapter 10 example 2: Text at absolute positions");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1002.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we grab the ContentByte and do some stuff with it
				PdfContentByte cb = writer.DirectContent;
            
				// first we draw some lines to be able to visualize the text alignment functions
				cb.SetLineWidth(0f);
				cb.MoveTo(250, 500);
				cb.LineTo(250, 800);
				cb.MoveTo(50, 700);
				cb.LineTo(400, 700);
				cb.MoveTo(50, 650);
				cb.LineTo(400, 650);
				cb.MoveTo(50, 600);
				cb.LineTo(400, 600);
				cb.Stroke();
            
				// we tell the ContentByte we're ready to draw text
				cb.BeginText();
            
				BaseFont bf = BaseFont.CreateFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				cb.SetFontAndSize(bf, 12);
				String text = "Sample text for alignment";
				// we show some text starting on some absolute position with a given alignment
				cb.ShowTextAligned(PdfContentByte.ALIGN_CENTER, text + " Center", 250, 700, 0);
				cb.ShowTextAligned(PdfContentByte.ALIGN_RIGHT, text + " Right", 250, 650, 0);
				cb.ShowTextAligned(PdfContentByte.ALIGN_LEFT, text + " Left", 250, 600, 0);
            
				// we draw some text on a certain position
				cb.SetTextMatrix(100, 400);
				cb.ShowText("Text at position 100,400.");
            
				// we draw some rotated text on a certain position
				cb.SetTextMatrix(0, 1, -1, 0, 100, 300);
				cb.ShowText("Text at position 100,300, rotated 90 degrees.");
            
				// we draw some mirrored, rotated text on a certain position
				cb.SetTextMatrix(0, 1, 1, 0, 200, 200);
				cb.ShowText("Text at position 200,200, mirrored and rotated 90 degrees.");
            
				// we tell the contentByte, we've finished drawing text
				cb.EndText();
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