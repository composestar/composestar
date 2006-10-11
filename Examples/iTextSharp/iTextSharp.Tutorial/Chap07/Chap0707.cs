using System;
using System.IO;
using System.util;

using iTextSharp.text;
using iTextSharp.text.pdf;
using iTextSharp.text.xml;
using System.Collections;
using iTextSharp.text.html;

namespace iTextSharp.tutorial.Chap07
{
	/// <summary>
	/// Chap0706 的摘要说明。
	/// </summary>
	public class Chap0707
	{
		public Chap0707()
		{
			Console.WriteLine("Chapter 7 example 7: parsing the HTML from example 2");
        
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4, 80, 50, 30, 65);
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a XML-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0707.pdf", FileMode.Create));
            
				//            // step 3: we create a parser and set the document handler
				//            Parser parser = ParserFactory.makeParser(PARSER);
				//            parser.setDocumentHandler(new SAXmyHtmlHandler(document));
				// step 3: we create a parser
				
				//			ITextHandler h = new ITextHandler(document);
				HtmlParser.Parse(document,"Chap0702.htm");
            
				// step 4: we parse the document _Document;
				//			h.Parse("Chap0702.htm");            
				// step 4: we parse the document
				//            parser.parse("Chap0702.html");
            
			}
			catch(Exception e) 
			{
				Console.Error.WriteLine(e.Message);
				Console.Error.WriteLine(e.StackTrace);
			}
		}
	}
}
