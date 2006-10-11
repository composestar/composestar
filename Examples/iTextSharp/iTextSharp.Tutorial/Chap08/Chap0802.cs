using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.rtf;

/**
 * This example creates a RTF document with two chapters and different headers
 * for both chapters. The same works for footers. Just replace setHeader with
 * setFooter.
 *
 * @author <a href="mailto:mhall@myrealbox.com">Mark.Hall@myrealbox.com</a>
 */
namespace iTextSharp.tutorial.Chap08
{
	public class Chap0802 
	{

		public  Chap0802() 
		{

			Console.WriteLine("Chapter 8 example 2: Headers in RTF");

			// step 1: creation of a document-object
			Document document = new Document();

			try 
			{

				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				RtfWriter.GetInstance(document, new FileStream("Chap0802.rtf", FileMode.Create));

				// step 3: we open the document
				document.Open();

				// step 4: we create two chapters and add the same content to both.
				Paragraph par = new Paragraph("This is some sample content.");
				Chapter chap1 = new Chapter("Chapter 1", 1);
				chap1.Add(par);
				Chapter chap2 = new Chapter("Chapter 2", 2);
				chap2.Add(par);

				// step 5: we create the header for the first chapter, set the header and
				// then add the first chapter.
				HeaderFooter hf1 = new HeaderFooter(new Phrase("This is chapter 1"), false);
				document.Header = hf1;
				document.Add(chap1);

				// step 6: we create a second header, set this one and then add the second
				// chapter.
				HeaderFooter hf2 = new HeaderFooter(new Phrase("This is chapter 2"), false);
				document.Header = hf2;
				document.Add(chap2);
			}
			catch(DocumentException de) 
			{
				Console.Error.WriteLine(de.Message);
			}
			catch(IOException ioe) 
			{
				Console.Error.WriteLine(ioe.Message);
			}

			// step 7: we close the document
			document.Close();
		}
	}
}