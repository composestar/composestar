using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap01
{
	/// <summary>
	/// Chap0109 的摘要说明。
	/// </summary>
	public class Chap0109
	{
		public Chap0109()
		{
			Console.WriteLine("Chapter 1 example 9: encryption 40 bits");
        
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			try 
			{
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap0109.pdf", FileMode.Create));
				writer.SetEncryption(PdfWriter.STRENGTH40BITS, null, null, PdfWriter.AllowCopy);
				document.Open();
				document.Add(new Paragraph("This document is Top Secret!"));
				document.Close();
			}
			catch (Exception de) 
			{
				Console.WriteLine(de.StackTrace);
			}

		}
	}
}
