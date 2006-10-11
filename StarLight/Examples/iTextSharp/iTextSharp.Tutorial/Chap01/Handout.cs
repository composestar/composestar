using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap01
{
	/// <summary>
	/// Handout 的摘要说明。
	/// </summary>
	public class Handout
	{
		public Handout(string[] args)
		{
			if (args.Length != 3) 
			{
				Console.Error.WriteLine("This tools needs 3 parameters:\njava Handout srcfile destfile pages");
			}
			else 
			{
				try 
				{
					int pages = int.Parse(args[2]);
					if (pages < 2 || pages > 8) 
					{
						throw new DocumentException("You can't have " + pages + " pages on one page (minimum 2; maximum 8).");
					}
                
					float x1 = 30f;
					float x2 = 280f;
					float x3 = 320f;
					float x4 = 565f;
                
					float[] y1 = new float[pages];
					float[] y2 = new float[pages];
                
					float height = (778f - (20f * (pages - 1))) / pages;
					y1[0] = 812f;
					y2[0] = 812f - height;
                
					for (int i = 1; i < pages; i++) 
					{
						y1[i] = y2[i - 1] - 20f;
						y2[i] = y1[i] - height;
					}
                
					// we create a reader for a certain document
					PdfReader reader = new PdfReader(args[0]);
					// we retrieve the total number of pages
					int n = reader.NumberOfPages;
					Console.WriteLine("There are " + n + " pages in the original file.");
                
					// step 1: creation of a document-object
					Document document = new Document(PageSize.A4);
					// step 2: we create a writer that listens to the document
					PdfWriter writer = PdfWriter.GetInstance(document, new FileStream(args[1], FileMode.Create));
					// step 3: we open the document
					document.Open();
					PdfContentByte cb = writer.DirectContent;
					PdfImportedPage page;
					int rotation;
					int j = 0;
					int p = 0;
					// step 4: we add content
					while (j < n) 
					{
						j++;
						Rectangle rect = reader.GetPageSizeWithRotation(j);
						float factorx = (x2 - x1) / rect.Width;
						float factory = (y1[p] - y2[p]) / rect.Height;
						float factor = (factorx < factory ? factorx : factory);
						float dx = (factorx == factor ? 0f : ((x2 - x1) - rect.Width * factor) / 2f);
						float dy = (factory == factor ? 0f : ((y1[p] - y2[p]) - rect.Height * factor) / 2f);
						page = writer.GetImportedPage(reader, j);
						rotation = reader.GetPageRotation(j);
						if (rotation == 90 || rotation == 270) 
						{
							cb.AddTemplate(page, 0, -factor, factor, 0, x1 + dx, y2[p] + dy + rect.Height * factor);
						}
						else 
						{
							cb.AddTemplate(page, factor, 0, 0, factor, x1 + dx, y2[p] + dy);
						}
						cb.SetRGBColorStroke(0xC0, 0xC0, 0xC0);
						cb.Rectangle(x3 - 5f, y2[p] - 5f, x4 - x3 + 10f, y1[p] - y2[p] + 10f);
						for (float l = y1[p] - 19; l > y2[p]; l -= 16) 
						{
							cb.MoveTo(x3, l);
							cb.LineTo(x4, l);
						}
						cb.Rectangle(x1 + dx, y2[p] + dy, rect.Width * factor, rect.Height * factor);
						cb.Stroke();
						Console.WriteLine("Processed page " + j);
						p++;
						if (p == pages) 
						{
							p = 0;
							document.NewPage();
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
