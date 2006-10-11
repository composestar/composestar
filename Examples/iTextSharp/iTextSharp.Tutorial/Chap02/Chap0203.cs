using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap02
{
	/// <summary>
	/// Chap0203 的摘要说明。
	/// </summary>
	public class Chap0203
	{
		public Chap0203()
		{
        
			Console.WriteLine("Chapter 2 example 3: Greek Characters");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0203.pdf",FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we Add a paragraph to the document
				document.Add(new Phrase("What is the " + (char) 945 + "-coefficient of the "
					+ (char) 946 + "-factor in the " + (char) 947 + "-equation?\n"));
				for (int i = 913; i < 970; i++) 
				{
					document.Add(new Phrase(" " + i.ToString() + ": " + (char) i));
				}
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
