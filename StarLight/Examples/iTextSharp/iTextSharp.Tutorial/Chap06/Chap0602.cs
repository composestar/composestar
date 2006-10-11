using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap06
{
	public class Chap0602 
	{
    
		public Chap0602() 
		{
        
			Console.WriteLine("Chapter 6 example 2: Adding a Wmf, Gif, Jpeg and Png-file using filenames");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0602.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				Image gif = Image.GetInstance("vonnegut.gif");
				Image jpeg = Image.GetInstance("myKids.jpg");
				Image png = Image.GetInstance("hitchcock.png");
            
				document.Add(gif);
				document.Add(jpeg);
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