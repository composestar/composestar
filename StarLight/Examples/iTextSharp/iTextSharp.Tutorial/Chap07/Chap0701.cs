using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.xml;

namespace iTextSharp.tutorial.Chap07
{
	public class Chap0701 
	{
    
		public  Chap0701() 
		{
        
			Console.WriteLine("Chapter 7 example 1: my first XML");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
            
				// step 2:
				// we create a writer that listens to the document
				// and directs a XML-stream to a file
				XmlWriter.GetInstance(document, new FileStream("Chap0701.xml", FileMode.Create), "itext.dtd");
            
				// step 3: we open the document
				document.Open();
            
				// step 4: we add content to the document
				Paragraph paragraph = new Paragraph("Please visit my ");
				Anchor anchor1 = new Anchor("website (external reference)", FontFactory.GetFont(FontFactory.HELVETICA, 12, Font.UNDERLINE, new Color(0, 0, 255)));
				anchor1.Reference = "http://www.lowagie.com/iText/";
				anchor1.Name = "top";
				paragraph.Add(anchor1);
				document.Add(paragraph);
            
				Paragraph entities = new Paragraph("These are some special characters: <, >, &, \" and '");
				document.Add(entities);
            
				document.Add(new Paragraph("some books I really like:"));
				List list;
				ListItem listItem;
				list = new List(true, 15);
				listItem = new ListItem("When Harlie was one", FontFactory.GetFont(FontFactory.TIMES_ROMAN, 12));
				listItem.Add(new Chunk(" by David Gerrold", FontFactory.GetFont(FontFactory.TIMES_ROMAN, 11, Font.ITALIC)).SetTextRise(8.0f));
				list.Add(listItem);
				listItem = new ListItem("The World according to Garp", FontFactory.GetFont(FontFactory.TIMES_ROMAN, 12));
				listItem.Add(new Chunk(" by John Irving", FontFactory.GetFont(FontFactory.TIMES_ROMAN, 11, Font.ITALIC)).SetTextRise(-8.0f));
				list.Add(listItem);
				listItem = new ListItem("Decamerone", FontFactory.GetFont(FontFactory.TIMES_ROMAN, 12));
				listItem.Add(new Chunk(" by Giovanni Boccaccio", FontFactory.GetFont(FontFactory.TIMES_ROMAN, 11, Font.ITALIC)));
				list.Add(listItem);
				document.Add(list);
            
				paragraph = new Paragraph("some movies I really like:");
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
				list.ListSymbol = new Chunk("*", FontFactory.GetFont(FontFactory.HELVETICA, 20, Font.BOLD));
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
            
				paragraph = new Paragraph("\n\n");
				document.Add(paragraph);
            
				Table table = new Table(3);
				table.BorderWidth = 1;
				table.BorderColor = new Color(0, 0, 255);
				table.Padding = 5;
				table.Spacing = 5;
				Cell cell = new Cell("header");
				cell.Header = true;
				cell.Colspan = 3;
				table.AddCell(cell);
				table.EndHeaders();
				cell = new Cell("example cell with colspan 1 and rowspan 2");
				cell.Rowspan = 2;
				cell.BorderColor = new Color(255, 0, 0);
				table.AddCell(cell);
				table.AddCell("1.1");
				table.AddCell("2.1");
				table.AddCell("1.2");
				table.AddCell("2.2");
				table.AddCell("cell test1");
				cell = new Cell("big cell");
				cell.Rowspan = 2;
				cell.Colspan = 2;
				table.AddCell(cell);
				table.AddCell("cell test2");
				document.Add(table);
            
				Image jpeg = Image.GetInstance("myKids.jpg");
				document.Add(jpeg);
				Image png = Image.GetInstance(new Uri("http://www.lowagie.com/iText/examples/hitchcock.png"));
				document.Add(png);
				Anchor anchor2 = new Anchor("please jump to a local destination", FontFactory.GetFont(FontFactory.HELVETICA, 12, Font.NORMAL, new Color(0, 0, 255)));
				anchor2.Reference = "#top";
				document.Add(anchor2);
            
				document.Add(paragraph);
            
				// we define some fonts
				Font chapterFont = FontFactory.GetFont(FontFactory.HELVETICA, 24, Font.NORMAL, new Color(255, 0, 0));
				Font sectionFont = FontFactory.GetFont(FontFactory.HELVETICA, 20, Font.NORMAL, new Color(0, 0, 255));
				Font subsectionFont = FontFactory.GetFont(FontFactory.HELVETICA, 18, Font.BOLD, new Color(0, 64, 64));
				// we create some paragraphs
				Paragraph blahblah = new Paragraph("blah blah blah blah blah blah blaah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah");
				Paragraph blahblahblah = new Paragraph("blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blaah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah");
            
				// this loop will create 7 chapters
				for (int i = 1; i < 8; i++) 
				{
					Paragraph cTitle = new Paragraph("This is chapter " + i, chapterFont);
					Chapter chapter = new Chapter(cTitle, i);
                
					if (i == 4) 
					{
						blahblahblah.Alignment = Element.ALIGN_JUSTIFIED;
						blahblah.Alignment = Element.ALIGN_JUSTIFIED;
						chapter.Add(blahblah);
					}
					if (i == 5) 
					{
						blahblahblah.Alignment = Element.ALIGN_CENTER;
						blahblah.Alignment = Element.ALIGN_RIGHT;
						chapter.Add(blahblah);
					}
					// add a table in the 6th chapter
					if (i == 6) 
					{
						blahblah.Alignment = Element.ALIGN_JUSTIFIED;
						chapter.Add(table);
					}
					// in every chapter 3 sections will be added
					for (int j = 1; j < 4; j++) 
					{
						Paragraph sTitle = new Paragraph("This is section " + j + " in chapter " + i, sectionFont);
						Section section = chapter.AddSection(sTitle, 1);
						// in all chapters except the 1st one, some extra text is added to section 3
						if (j == 3 && i > 1) 
						{
							section.Add(blahblah);
						}
						// in every section 3 subsections are added
						for (int k = 1; k < 4; k++) 
						{
							Paragraph subTitle = new Paragraph("This is subsection " + k + " of section " + j, subsectionFont);
							Section subsection = section.AddSection(subTitle, 3);
							if (k == 1 && j == 3) 
							{
								subsection.Add(blahblahblah);
								subsection.Add(table);
							}
							subsection.Add(blahblah);
						}
						if (j == 2 && i > 2) 
						{
							section.Add(blahblahblah);
							section.Add(table);
						}
					}
					document.Add(chapter);
				}
            
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