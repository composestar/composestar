using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap06
{
	public class Chap0607 
	{
    
		public Chap0607() 
		{
        
			Console.WriteLine("Chapter 6 example 7: Scaling an Image");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
            
				PdfWriter.GetInstance(document, new FileStream("Chap0607.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we add content
				Image jpg1 = Image.GetInstance("myKids.jpg");
				jpg1.ScaleAbsolute(97, 101);
				document.Add(new Paragraph("scaleAbsolute(97, 101)"));
				document.Add(jpg1);
				Image jpg2 = Image.GetInstance("myKids.jpg");
				jpg2.ScalePercent(50);
				document.Add(new Paragraph("scalePercent(50)"));
				document.Add(jpg2);
				Image jpg3 = Image.GetInstance("myKids.jpg");
				jpg3.ScaleAbsolute(194, 101);
				document.Add(new Paragraph("scaleAbsolute(194, 101)"));
				document.Add(jpg3);
				Image jpg4 = Image.GetInstance("myKids.jpg");
				jpg4.ScalePercent(100, 50);
				document.Add(new Paragraph("scalePercent(100, 50)"));
				document.Add(jpg4);
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