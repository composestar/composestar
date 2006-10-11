using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap09
{
	public class Chap0904 
	{
		public Chap0904() 
		{
        
			Console.WriteLine("Chapter 9 example 4: True Type Collections");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
				String[] names = BaseFont.EnumerateTTCNames("c:\\winnt\\fonts\\msgothic.ttc");
				for (int i = 0; i < names.Length; i++) 
				{
					Console.WriteLine("font " + i + ": " + names[i]);                
				}
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0904.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we add content to the document
				BaseFont bf = BaseFont.CreateFont("c:\\winnt\\fonts\\msgothic.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				Console.WriteLine("postscriptname: " + bf.PostscriptFontName);
				Font font = new Font(bf, 16);
				String text1 = "\u5951\u7d04\u8005\u4f4f\u6240\u30e9\u30a4\u30f3\uff11";
				String text2 = "\u5951\u7d04\u8005\u96fb\u8a71\u756a\u53f7";
				document.Add(new Paragraph(text1, font));
				document.Add(new Paragraph(text2, font));
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