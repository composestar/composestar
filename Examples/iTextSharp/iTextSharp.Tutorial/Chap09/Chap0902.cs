using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap09
{
	public class Chap0902 
	{
    
		public Chap0902() 
		{
        
			Console.WriteLine("Chapter 9 example 2: True Type fonts (not embedded)");        
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0902.pdf",FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we Add content to the document
				BaseFont bfComic = BaseFont.CreateFont("c:\\winnt\\fonts\\comicbd.ttf", BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				Console.WriteLine("postscriptname: " + bfComic.PostscriptFontName);
				String[] codePages = bfComic.CodePagesSupported;
				for (int i = 0; i < codePages.Length; i++) 
				{
					Console.WriteLine(codePages[i]);                
				}
				string[][] names = bfComic.FullFontName;
				for (int k = 0; k < names.Length; ++k) 
				{
					if (names[k][0].Equals("3") && names[k][1].Equals("1")) // Microsoft encoding
						Console.WriteLine(names[k][3]);
				}
            
            
				Font font = new Font(bfComic, 12);
				String text1 = "This is the quite popular True Type font 'Comic'.";
				document.Add(new Paragraph(text1, font));
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