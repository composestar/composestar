using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap03
{
	/// <summary>
	/// Chap0301 的摘要说明。
	/// </summary>
	public class Chap0301
	{
		public Chap0301()
		{
			Console.WriteLine("Chapter 3 example 1: Anchors");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0301.pdf", FileMode.Create));
            
				// step 3: we Open the document
				document.Open();
            
				// step 4:
				Paragraph paragraph = new Paragraph("Please visit my ");
				Anchor anchor1 = new Anchor("website (external reference)", FontFactory.GetFont(FontFactory.HELVETICA, 12, Font.UNDERLINE, new Color(0, 0, 255)));
				anchor1.Reference = "http://itextsharp.sourceforge.net";
				anchor1.Name = "top";
				paragraph.Add(anchor1);
				paragraph.Add(new Chunk(".\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"));
				document.Add(paragraph);
				Anchor anchor2 = new Anchor("please jump to a local destination", FontFactory.GetFont(FontFactory.HELVETICA, 12, Font.NORMAL, new Color(0, 0, 255)));
				anchor2.Reference = "#top";
				document.Add(anchor2);
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
