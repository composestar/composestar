using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap04
{
	/// <summary>
	/// Chap0401 的摘要说明。
	/// </summary>
	public class Chap0401
	{
		public Chap0401()
		{
			Console.WriteLine("Chapter 4 example 1: Headers en Footers");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
				// step 2: we create a writer that listens to the document
				PdfWriter.GetInstance(document, new FileStream("Chap0401.pdf", FileMode.Create));
            
				// we Add a Footer that will show up on PAGE 1
				HeaderFooter footer = new HeaderFooter(new Phrase("This is page: "), true);
				footer.Border = Rectangle.NO_BORDER;
				document.Footer = footer;
            
				// step 3: we open the document
				document.Open();
            
				// we Add a Header that will show up on PAGE 2
				HeaderFooter header = new HeaderFooter(new Phrase("This is a header"), false);
				document.Header = header;
            
				// step 4: we Add content to the document
            
				// PAGE 1
				document.Add(new Paragraph("Hello World"));
				// we trigger a page break
				document.NewPage();
            
				// PAGE 2
				// we Add some more content
				document.Add(new Paragraph("Hello Earth"));
				// we remove the header starting from PAGE 3
				document.ResetHeader();
				// we trigger a page break
				document.NewPage();
            
				// PAGE 3
				// we Add some more content
				document.Add(new Paragraph("Hello Sun"));
				document.Add(new Paragraph("Remark: the header has vanished!"));
				// we reset the page numbering
				document.ResetPageCount();
				// we trigger a page break
				document.NewPage();
            
				// PAGE 4
				// we Add some more content
				document.Add(new Paragraph("Hello Moon"));
				document.Add(new Paragraph("Remark: the pagenumber has been reset!"));
            
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
