using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;


namespace iTextSharp.tutorial.Chap01
{
	/// <summary>
	/// Chap0110 的摘要说明。
	/// </summary>
	public class Chap0110
	{
		public Chap0110()
		{
			Console.WriteLine("Chapter 1 example 10: encryption 128 bits");
        
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			try 
			{
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap0110.pdf", FileMode.Create));
				writer.SetEncryption(PdfWriter.STRENGTH128BITS, "userpass", "ownerpass", PdfWriter.AllowCopy | PdfWriter.AllowPrinting);
				document.Open();
				document.Add(new Paragraph("This document is Top Secret!"));
				document.Close();
			}
			catch (Exception de) 
			{
				Console.Error.WriteLine(de.StackTrace);
			}

		}
	}
}
