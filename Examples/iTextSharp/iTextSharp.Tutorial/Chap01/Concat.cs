using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;



namespace iTextSharp.tutorial.Chap01
{
	/// <summary>
	/// Concat 的摘要说明。
	/// </summary>
	public class Concat
	{
		public Concat(String[] args)
		{
			if (args.Length < 3) 
			{
				Console.Error.WriteLine("This tools needs at least 3 parameters:\njava Concat destfile file1 file2 [file3 ...]");
			}
			else 
			{
				try 
				{
					int f = 1;
					// we create a reader for a certain document
					PdfReader reader = new PdfReader(args[f]);
					// we retrieve the total number of pages
					int n = reader.NumberOfPages;
					Console.WriteLine("There are " + n + " pages in the original file.");
                
					// step 1: creation of a document-object
					Document document = new Document(reader.GetPageSizeWithRotation(1));
					// step 2: we create a writer that listens to the document
					PdfWriter writer = PdfWriter.GetInstance(document, new FileStream(args[0], FileMode.Create));
					// step 3: we open the document
					document.Open();
					PdfContentByte cb = writer.DirectContent;
					PdfImportedPage page;
					int rotation;
					// step 4: we add content
					while (f < args.Length) 
					{
						int i = 0;
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
						f++;
						if (f < args.Length) 
						{
							reader = new PdfReader(args[f]);
							// we retrieve the total number of pages
							n = reader.NumberOfPages;
							Console.WriteLine("There are " + n + " pages in the original file.");
						}
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
