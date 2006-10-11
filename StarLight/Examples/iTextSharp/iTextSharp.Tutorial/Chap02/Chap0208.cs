using System;
using System.IO;
using System.Collections;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap02
{
	/// <summary>
	///Chapter 2 example 8: split character
	/// </summary>
	public class Chap0208: ISplitCharacter 
	{
		public void Chap0208_()
		{
			Console.WriteLine("Chapter 2 example 8: split character");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap0208.pdf", FileMode.Create));
            
				// step 3: we Open the document
				document.Open();
            
				// step 4:
				// we Add some content
				String text = "Some.text.to.show.the.splitting.action.of.the.interface.";
				Chap0208 split = new Chap0208();
				Chunk ck = new Chunk(text, FontFactory.GetFont(FontFactory.HELVETICA, 24));
				Paragraph p = new Paragraph(24, ck);
				document.Add(new Paragraph("Normal split."));
				document.Add(p);
				ck = new Chunk(text, FontFactory.GetFont(FontFactory.HELVETICA, 24));
				
				ck.SetSplitCharacter(split);
				
				p = new Paragraph(24, ck);
				document.Add(new Paragraph("The dot '.' is the split character."));
				document.Add(p);
            
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
    
		/**
			 * Returns <CODE>true</CODE> if the character can split a line.
			 * @param c the character
			 * @return <CODE>true</CODE> if the character can split a line
			 */
//		bool IsSplitCharacter(int start, int current, int end, char[] cc, PdfChunk[] ck)
//		{
//		
//		}
		public bool IsSplitCharacter(int start, int current, int end, char[] cc, PdfChunk[] ck)
		{
			return (cc[0] == '.');
		}

		public bool IsSplitCharacter(char c) 
		{
			return (c == '.');
		}
	}

}
