using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap11
{
	public class Chap1107 
	{
    
		public Chap1107() 
		{
        
			Console.WriteLine("Chapter 11 example 7: Outlines and Destinations");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1107.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we grab the ContentByte and do some stuff with it
				PdfContentByte cb = writer.DirectContent;
            
				// we create a PdfTemplate
				PdfTemplate template = cb.CreateTemplate(25, 25);
            
				// we add some crosses to visualize the destinations
				template.MoveTo(13, 0);
				template.LineTo(13, 25);
				template.MoveTo(0, 13);
				template.LineTo(50, 13);
				template.Stroke();
            
				// we add the template on different positions
				cb.AddTemplate(template, 287, 787);
				cb.AddTemplate(template, 187, 487);
				cb.AddTemplate(template, 487, 287);
				cb.AddTemplate(template, 87, 87);
            
				// we define the destinations
				PdfDestination d1 = new PdfDestination(PdfDestination.XYZ, 300, 800, 0);
				PdfDestination d2 = new PdfDestination(PdfDestination.FITH, 500);
				PdfDestination d3 = new PdfDestination(PdfDestination.FITR, 200, 300, 400, 500);
				PdfDestination d4 = new PdfDestination(PdfDestination.FITBV, 100);
				PdfDestination d5 = new PdfDestination(PdfDestination.FIT);
            
				// we define the outlines
				PdfOutline out1 = new PdfOutline(cb.RootOutline, d1, "root");
				PdfOutline out2 = new PdfOutline(out1, d2, "sub 1");
				PdfOutline out3 = new PdfOutline(out1, d3, "sub 2");
				PdfOutline out4 = new PdfOutline(out2, d4, "sub 2.1");
				PdfOutline out5 = new PdfOutline(out2, d5, "sub 2.2");
            
				cb.AddOutline(out1);
				cb.AddOutline(out2);
				cb.AddOutline(out3);
				cb.AddOutline(out4);
				cb.AddOutline(out5);
            
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