using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap13
{
	/// <summary>
	/// Chap13_pdfreader 的摘要说明。
	/// </summary>
	public class Chap13_pdfreader
	{
		public Chap13_pdfreader()
		{
        Console.WriteLine("Chapter 13 example pdfreader: reading an existing PDF file");
        try {
            // we create a reader for a certain document
            PdfReader reader = new PdfReader("Chap0702.pdf");
            // we retrieve the total number of pages
            int n = reader.NumberOfPages;
            // we retrieve the size of the first page
            Rectangle psize = reader.GetPageSize(1);
            float width = psize.Width;
            float height = psize.Height;
            
            // step 1: creation of a document-object
            Document.Compress = true;
            Document document = new Document(psize, 50, 50, 50, 50);
            // step 2: we create a writer that listens to the document
            PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap13_pdfreader.pdf", FileMode.Create));
            // step 3: we open the document
            try {
                Watermark watermark = new Watermark(Image.GetInstance("watermark.jpg"), 200, 320);
                document.Add(watermark);
            }
            catch(Exception e) {
                Console.WriteLine("Are you sure you have the file 'watermark.jpg' in the right path?");
            }
            document.Open();
            // step 4: we add content
            PdfContentByte cb = writer.DirectContent;
            int i = 0;
            int p = 0;
            Console.WriteLine("There are " + n + " pages in the document.");
            while (i < n) {
                document.NewPage();
                p++;
                i++;
                PdfImportedPage page1 = writer.GetImportedPage(reader, i);
                cb.AddTemplate(page1, .5f, 0, 0, .5f, 0, height / 2);
               Console.WriteLine("processed page " + i);
                if (i < n) {
                    i++;
                    PdfImportedPage page2 = writer.GetImportedPage(reader, i);
                    cb.AddTemplate(page2, .5f, 0, 0, .5f, width / 2, height / 2);
                   Console.WriteLine("processed page " + i);
                }
                if (i < n) {
                    i++;
                    PdfImportedPage page3 = writer.GetImportedPage(reader, i);
                    cb.AddTemplate(page3, .5f, 0, 0, .5f, 0, 0);
                   Console.WriteLine("processed page " + i);
                }
                if (i < n) {
                    i++;
                    PdfImportedPage page4 = writer.GetImportedPage(reader, i);
                    cb.AddTemplate(page4, .5f, 0, 0, .5f, width / 2, 0);
                   Console.WriteLine("processed page " + i);
                }
                cb.SetRGBColorStroke(255, 0, 0);
                cb.MoveTo(0, height / 2);
                cb.LineTo(width, height / 2);
                cb.Stroke();
                cb.MoveTo(width / 2, height);
                cb.LineTo(width / 2, 0);
                cb.Stroke();
                BaseFont bf = BaseFont.CreateFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                cb.BeginText();
                cb.SetFontAndSize(bf, 14);
                cb.ShowTextAligned(PdfContentByte.ALIGN_CENTER, "page " + p + " of " + ((n / 4) + (n % 4 > 0? 1 : 0)), width / 2, 40, 0);
                cb.EndText();
            }
            // step 5: we close the document
            document.Close();
        }
        catch (Exception de) {
            Console.WriteLine(de.StackTrace);
        }
		}
	}
}
