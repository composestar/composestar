using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap09
{
	public class Chap0907 
	{
		public Chap0907() 
		{
        
			Console.WriteLine("Chapter 9 example 7: Barcodes without ttf");
        
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
            
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap0907.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we add content to the document
				PdfContentByte cb = writer.DirectContent;
				Barcode39 code39 = new Barcode39();
				code39.Code = "CODE39-1234567890";
				code39.StartStopText = false;
				Image image39 = code39.CreateImageWithBarcode(cb, null, null);
				Barcode39 code39ext = new Barcode39();
				code39ext.Code = "The willows.";
				code39ext.StartStopText = false;
				code39ext.Extended = true;
				Image image39ext = code39ext.CreateImageWithBarcode(cb, null, null);
				Barcode128 code128 = new Barcode128();
				code128.Code = "1Z234786 hello";
				Image image128 = code128.CreateImageWithBarcode(cb, null, null);
				BarcodeEAN codeEAN = new BarcodeEAN();
				codeEAN.CodeType = BarcodeEAN.EAN13;
				codeEAN.Code = "9780201615883";
				Image imageEAN = codeEAN.CreateImageWithBarcode(cb, null, null);
				BarcodeInter25 code25 = new BarcodeInter25();
				code25.GenerateChecksum = true;
				code25.Code = "41-1200076041-001";
				Image image25 = code25.CreateImageWithBarcode(cb, null, null);
				BarcodePostnet codePost = new BarcodePostnet();
				codePost.Code = "12345";
				Image imagePost = codePost.CreateImageWithBarcode(cb, null, null);
				BarcodePostnet codePlanet = new BarcodePostnet();
				codePlanet.Code = "50201402356";
				codePlanet.CodeType = BarcodePostnet.PLANET;
				Image imagePlanet = codePlanet.CreateImageWithBarcode(cb, null, null);
				PdfTemplate tp = cb.CreateTemplate(0, 0);
				PdfTemplate ean = codeEAN.CreateTemplateWithBarcode(cb, null, new Color(System.Drawing.Color.Blue));
				BarcodeEAN codeSUPP = new BarcodeEAN();
				codeSUPP.CodeType = BarcodeEAN.SUPP5;
				codeSUPP.Code = "54995";
				codeSUPP.Baseline = -2;
				BarcodeEANSUPP eanSupp = new BarcodeEANSUPP(codeEAN, codeSUPP);
				Image imageEANSUPP = eanSupp.CreateImageWithBarcode(cb, null, new Color(System.Drawing.Color.Blue));
				PdfPTable table = new PdfPTable(2);
				table.WidthPercentage = 100;
				table.DefaultCell.Border = Rectangle.NO_BORDER;
				table.DefaultCell.HorizontalAlignment = Element.ALIGN_CENTER;
				table.DefaultCell.VerticalAlignment = Element.ALIGN_MIDDLE;
				table.DefaultCell.FixedHeight = 70;
				table.AddCell("CODE 39");
				table.AddCell(new Phrase(new Chunk(image39, 0, 0)));
				table.AddCell("CODE 39 EXTENDED");
				table.AddCell(new Phrase(new Chunk(image39ext, 0, 0)));
				table.AddCell("CODE 128");
				table.AddCell(new Phrase(new Chunk(image128, 0, 0)));
				table.AddCell("CODE EAN");
				table.AddCell(new Phrase(new Chunk(imageEAN, 0, 0)));
				table.AddCell("CODE EAN\nWITH\nSUPPLEMENTAL 5");
				table.AddCell(new Phrase(new Chunk(imageEANSUPP, 0, 0)));
				table.AddCell("CODE INTERLEAVED");
				table.AddCell(new Phrase(new Chunk(image25, 0, 0)));
				table.AddCell("CODE POSTNET");
				table.AddCell(new Phrase(new Chunk(imagePost, 0, 0)));
				table.AddCell("CODE PLANET");
				table.AddCell(new Phrase(new Chunk(imagePlanet, 0, 0)));
				document.Add(table);
			}
			catch (Exception de) 
			{
				Console.Error.WriteLine(de.StackTrace);
			}
        
			// step 5: we close the document
			document.Close();
		}
	}
}