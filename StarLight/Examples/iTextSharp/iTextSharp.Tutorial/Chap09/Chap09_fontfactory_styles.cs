using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap09
{
	/// <summary>
	/// Chap09_fontfactory_styles 的摘要说明。
	/// </summary>
	public class Chap09_fontfactory_styles
	{
		public Chap09_fontfactory_styles()
		{
        Console.WriteLine("Chapter 9: class FontFactory and Styles");
        
        // step 1: creation of a document-object
        Document document = new Document();
        
        try {
            
            // step 2:
            // we create a writer that listens to the document
            // and directs a PDF-stream to a file
            PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap09_fontfactory_styles.pdf", FileMode.Create));
            
            // step 3: we open the document
            document.Open();
            
            // step 4:
            // we add some content
            FontFactory.Register("c:\\winnt\\fonts\\arial.ttf");
            FontFactory.Register("c:\\winnt\\fonts\\arialbd.ttf");
            FontFactory.Register("c:\\winnt\\fonts\\ariali.ttf");
            FontFactory.Register("c:\\winnt\\fonts\\arialbi.ttf");
            Phrase myPhrase = new Phrase("This is font family Arial ", FontFactory.GetFont("Arial", 8));
            myPhrase.Add(new Phrase("italic ", FontFactory.GetFont("Arial", 8, Font.ITALIC)));
            myPhrase.Add(new Phrase("bold ", FontFactory.GetFont("Arial", 8, Font.BOLD)));
            myPhrase.Add(new Phrase("bolditalic", FontFactory.GetFont("Arial", 8, Font.BOLDITALIC)));
            document.Add(myPhrase);
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
