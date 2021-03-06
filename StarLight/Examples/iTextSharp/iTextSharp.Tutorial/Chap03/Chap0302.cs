using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap03
{
	/// <summary>
	/// Chap0302 ??ժҪ˵????
	/// </summary>
	public class Chap0302
	{
		public Chap0302()
		{
			Console.WriteLine("Chapter 3 example 2: Lists");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a PDF-stream to a file
				PdfWriter.GetInstance(document, new FileStream("Chap0302.pdf", FileMode.Create));
            
				// step 3: we Open the document
				document.Open();
            
				// step 4:
            
				List list = new List(true, 20);
				list.Add(new ListItem("First line"));
				list.Add(new ListItem("The second line is longer to see what happens once the end of the line is reached. Will it start on a new line?"));
				list.Add(new ListItem("Third line"));
				document.Add(list);
            
				document.Add(new Paragraph("some books I really like:"));
				ListItem listItem;
				list = new List(true, 15);
				listItem = new ListItem("When Harlie was one", FontFactory.GetFont(FontFactory.TIMES_ROMAN, 12));
				listItem.Add(new Chunk(" by David Gerrold", FontFactory.GetFont(FontFactory.TIMES_ROMAN, 11, Font.ITALIC)));
				list.Add(listItem);
				listItem = new ListItem("The World according to Garp", FontFactory.GetFont(FontFactory.TIMES_ROMAN, 12));
				listItem.Add(new Chunk(" by John Irving", FontFactory.GetFont(FontFactory.TIMES_ROMAN, 11, Font.ITALIC)));
				list.Add(listItem);
				listItem = new ListItem("Decamerone", FontFactory.GetFont(FontFactory.TIMES_ROMAN, 12));
				listItem.Add(new Chunk(" by Giovanni Boccaccio", FontFactory.GetFont(FontFactory.TIMES_ROMAN, 11, Font.ITALIC)));
				list.Add(listItem);
				document.Add(list);
            
				Paragraph paragraph = new Paragraph("some movies I really like:");
				list = new List(false, 10);
				list.Add("Wild At Heart");
				list.Add("Casablanca");
				list.Add("When Harry met Sally");
				list.Add("True Romance");
				list.Add("Le mari de la coiffeuse");
				paragraph.Add(list);
				document.Add(paragraph);
            
				document.Add(new Paragraph("Some authors I really like:"));
				list = new List(false, 20);
				list.ListSymbol = new Chunk("\u2022", FontFactory.GetFont(FontFactory.HELVETICA, 20, Font.BOLD));
				listItem = new ListItem("Isaac Asimov");
				list.Add(listItem);
				List sublist;
				sublist = new List(true, 10);
				sublist.ListSymbol = new Chunk("", FontFactory.GetFont(FontFactory.HELVETICA, 8));
				sublist.Add("The Foundation Trilogy");
				sublist.Add("The Complete Robot");
				sublist.Add("Caves of Steel");
				sublist.Add("The Naked Sun");
				list.Add(sublist);
				listItem = new ListItem("John Irving");
				list.Add(listItem);
				sublist = new List(true, 10);
				sublist.ListSymbol = new Chunk("", FontFactory.GetFont(FontFactory.HELVETICA, 8));
				sublist.Add("The World according to Garp");
				sublist.Add("Hotel New Hampshire");
				sublist.Add("A prayer for Owen Meany");
				sublist.Add("Widow for a year");
				list.Add(sublist);
				listItem = new ListItem("Kurt Vonnegut");
				list.Add(listItem);
				sublist = new List(true, 10);
				sublist.ListSymbol = new Chunk("", FontFactory.GetFont(FontFactory.HELVETICA, 8));
				sublist.Add("Slaughterhouse 5");
				sublist.Add("Welcome to the Monkey House");
				sublist.Add("The great pianola");
				sublist.Add("Galapagos");
				list.Add(sublist);
				document.Add(list);
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
