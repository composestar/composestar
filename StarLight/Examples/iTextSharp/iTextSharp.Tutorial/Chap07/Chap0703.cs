using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;
using iTextSharp.text.xml;

namespace iTextSharp.tutorial.Chap07
{
	public class Chap0703 
	{
    
		public  Chap0703() 
		{
        
			Console.WriteLine("Chapter 7 example 3: parsing an XML document with custom tags");
        
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4, 80, 50, 30, 65);
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a XML-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0703.pdf", FileMode.Create));
            
				// step 3: we create a parser and set the document handler
				
				ITextmyHandler h = new ITextmyHandler(document, new TagMap("tagmap0703.xml"));
            
				// step 4: we parse the document
				h.Parse("Chap0703.xml");
            
			}
			catch(Exception e) 
			{
				Console.Error.WriteLine(e.StackTrace);
				Console.Error.WriteLine(e.Message);
			}
		}
	}
}