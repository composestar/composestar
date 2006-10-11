using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap06
{
	public class Chap0613 
	{
		public Chap0613() 
		{
			Console.WriteLine("Chapter 6 example 13: masked images");
        
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			try 
			{
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap0613.pdf", FileMode.Create));
            
				document.Open();
				Paragraph p = new Paragraph("Some text behind a masked image.");
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
            
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				document.Add(p);
				PdfContentByte cb = writer.DirectContent;
				byte[] maskr = {(byte)0x3c, (byte)0x7e, (byte)0xe7, (byte)0xc3, (byte)0xc3, (byte)0xe7, (byte)0x7e, (byte)0x3c};
				Image mask = Image.GetInstance(8, 8, 1, 1, maskr);
				mask.MakeMask();
				mask.Inverted=true;
//				mask.InvertMask = true;
				Image image = Image.GetInstance("vonnegut.gif");
				image.ImageMask = mask;
				image.SetAbsolutePosition(60, 620);
				// explicit masking
				cb.AddImage(image);
				// stencil masking
				cb.SetRGBColorFill(255, 0, 0);
				cb.AddImage(mask, mask.ScaledWidth * 8, 0, 0, mask.ScaledHeight * 8, 100, 400);
				cb.SetRGBColorFill(0, 255, 0);
				cb.AddImage(mask, mask.ScaledWidth * 8, 0, 0, mask.ScaledHeight * 8, 200, 400);
				cb.SetRGBColorFill(0, 0, 255);
				cb.AddImage(mask, mask.ScaledWidth * 8, 0, 0, mask.ScaledHeight * 8, 300, 400);
				document.Close();
			}
			catch (Exception de) 
			{
				Console.Error.WriteLine(de.StackTrace);
			}
		}
	}
}