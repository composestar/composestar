using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.xml;
using iTextSharp.text.pdf;
using iTextSharp.text.html;
using iTextSharp.text.rtf;

namespace iTextSharp.tutorial.Chap07
{
	/// <summary>
	/// Chap0702 的摘要说明。
	/// </summary>
	public class Chap0702
	{
		public Chap0702()
		{
			Console.WriteLine("Chapter 7 example 2: parsing the result of example 1");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a XML-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0702.pdf", FileMode.Create));
				HtmlWriter.GetInstance(document,new FileStream("Chap0702.htm", FileMode.Create));
				RtfWriter.GetInstance(document,new FileStream("Chap0702.rtf", FileMode.Create));
				// step 3: we create a parser
				
				ITextHandler h = new ITextHandler(document);
            
				// step 4: we parse the document
				h.Parse("Chap0701.xml");

			}
			catch (Exception e) 
			{
				Console.Error.WriteLine(e.StackTrace);
				Console.Error.WriteLine(e.Message);
			}
		}
	}
}
