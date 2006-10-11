using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap06
{

	public class Chap0606 
	{
    
		public Chap0606() 
		{
        
			Console.WriteLine("Chapter 6 example 6: Absolute Positioning of an Image");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
            
				PdfWriter.GetInstance(document, new FileStream("Chap0606.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we add content
				Image png = Image.GetInstance("hitchcock.png");
				png.SetAbsolutePosition(171, 250);
				document.Add(png);
				png.SetAbsolutePosition(342, 500);
				document.Add(png);
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