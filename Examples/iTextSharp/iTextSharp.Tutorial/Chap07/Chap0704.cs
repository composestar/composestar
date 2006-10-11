using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;
using iTextSharp.text.xml;

namespace iTextSharp.tutorial.Chap07
{
	public class Chap0704 
	{
    
		public  Chap0704() 
		{
        
			Console.WriteLine("Chapter 7 example 4: making life easy");
        
			// step 1: creation of a document-object
			Document documentA = new Document(PageSize.A4, 80, 50, 30, 65);
        
			// step 1: creation of a document-object
			Document documentB = new Document(PageSize.A4, 80, 50, 30, 65);
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a XML-stream to a file
				PdfWriter.GetInstance(documentA, new FileStream("Chap0704a.pdf", FileMode.Create));
				PdfWriter.GetInstance(documentB, new FileStream("Chap0704b.pdf", FileMode.Create));
            
				// step 3: we parse the document
				XmlParser.Parse(documentA, "Chap0701.xml");
				XmlParser.Parse(documentB, "Chap0703.xml", "tagmap0703.xml");
            
			}
			catch(Exception e) 
			{
				Console.Error.WriteLine(e.Message);
				Console.Error.WriteLine(e.StackTrace);
			}
		}
	}
}