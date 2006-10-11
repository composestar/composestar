using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap01
{
	/// <summary>
	/// Encrypt 的摘要说明。
	/// </summary>
	public class Encrypt
	{
		public Encrypt(String[] args)
		{
			if (args.Length != 3) 
			{
				Console.Error.WriteLine("This tools needs 3 parameters:\njava Encrypt srcfile destfile password");
			}
			else 
			{
				try 
				{
					// we create a reader for a certain document
					PdfReader reader = new PdfReader(args[0]);
					// we retrieve the total number of pages
					int n = reader.NumberOfPages;
					Console.WriteLine("There are " + n + " pages in the original file.");
                
					// step 1: creation of a document-object
					Document document = new Document(reader.GetPageSizeWithRotation(1));
					// step 2: we create a writer that listens to the document
					PdfWriter writer = PdfWriter.GetInstance(document, new FileStream(args[1], FileMode.Create));
					writer.SetEncryption(PdfWriter.STRENGTH128BITS, args[2], null, PdfWriter.AllowPrinting);
					// step 3: we open the document
					document.Open();
					PdfContentByte cb = writer.DirectContent;
					PdfImportedPage page;
					int rotation;
					int i = 0;
					// step 4: we add content
					while (i < n) 
					{
						i++;
						document.SetPageSize(reader.GetPageSizeWithRotation(i));
						document.NewPage();
						page = writer.GetImportedPage(reader, i);
						rotation = reader.GetPageRotation(i);
						if (rotation == 90 || rotation == 270) 
						{
							cb.AddTemplate(page, 0, -1f, 1f, 0, 0, reader.GetPageSizeWithRotation(i).Height);
						}
						else 
						{
							cb.AddTemplate(page, 1f, 0, 0, 1f, 0, 0);
						}
						Console.WriteLine("Processed page " + i);
					}
					// step 5: we close the document
					document.Close();
				}
				catch(Exception e) 
				{
					Console.Error.WriteLine(e.Message);
					Console.Error.WriteLine(e.StackTrace);
				}
			}

		}
	}
}
