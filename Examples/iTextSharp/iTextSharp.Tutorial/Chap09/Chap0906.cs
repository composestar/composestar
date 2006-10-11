using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap09
{
	public class Chap0906 
	{
		public Chap0906() 
		{
        
			Console.WriteLine("Chapter 9 example 6: FontFactory");
        
			FontFactory.Register("c:\\winnt\\fonts\\comicbd.ttf");
			FontFactory.Register("c:\\winnt\\fonts\\comic.ttf");
			FontFactory.Register("c:\\winnt\\fonts\\msgothic.ttc");
			Console.WriteLine("These fonts were registered at the FontFactory");
			foreach(string fontName in FontFactory.RegisteredFonts) 
			{
				Console.WriteLine(fontName);
			}
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0906.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we add content to the document
				Font font0 = FontFactory.GetFont(BaseFont.HELVETICA, BaseFont.WINANSI, 12);
				String text0 = "This is the quite popular built in font '" + BaseFont.HELVETICA + "'.";
				document.Add(new Paragraph(text0, font0));
				Font font1 = FontFactory.GetFont("ComicSansMS", BaseFont.WINANSI, 12);
				String text1 = "This is the quite popular True Type font 'ComicSansMS'.";
				document.Add(new Paragraph(text1, font1));
				Font font2 = FontFactory.GetFont("ComicSansMS-Bold", BaseFont.WINANSI, 12);
				String text2 = "This is the quite popular True Type font 'ComicSansMS-Bold'.";
				document.Add(new Paragraph(text2, font2));
				Font font3= FontFactory.GetFont("MS-PGothic", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12);
				String text3 = "\u5951\u7d04\u8005\u4f4f\u6240\u30e9\u30a4\u30f3\uff11";
				document.Add(new Paragraph(text3, font3));
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