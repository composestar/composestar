using System;
using System.IO;
using iTextSharp.text;
using iTextSharp.text.pdf;
//using iTextSharp.text.html;
using iTextSharp.text.xml;
//using iTextSharp.text.rtf;

namespace iTextSharp.tutorial.Chap07
{
	/// <summary>
	/// Cv 的摘要说明。
	/// </summary>
	public class Cv
	{
		public Cv()
		{
        
        Console.WriteLine("Chapter 7 extra: my resume");
        
        // step 1: creation of a document-object
        Document document = new Document(PageSize.A4, 80, 50, 30, 65);
        
        try {
            
            // step 2:
            // we create a writer that listens to the document
            // and directs a XML-stream to a file
            PdfWriter.GetInstance(document, new FileStream("cv.pdf", FileMode.Create));
            
            // step 3: we parse the document
            XmlParser.Parse(document, "cv.xml");
            
        }
        catch(Exception e) {
            
            Console.WriteLine(e.Message);
        }
		}
	}
}
