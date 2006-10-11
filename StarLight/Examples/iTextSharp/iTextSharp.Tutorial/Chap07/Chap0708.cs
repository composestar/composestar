using System;
using iTextSharp.text;
using System.IO;
using iTextSharp.text.html;
using iTextSharp.text.markup;

namespace iTextSharp.tutorial.Chap07
{
	/// <summary>
	/// Chap0708 的摘要说明。
	/// </summary>
	public class Chap0708
	{
		public Chap0708()
		{
       Console.WriteLine("Chapter 7 example 8: HTML and CSS");
        
        // step 1: creation of a document-object
        Document document = new Document();
        
        try {
            
            // step 2:
            // we create a writer that listens to the document
            // and directs a XML-stream to a file
            HtmlWriter.GetInstance(document, new FileStream("Chap0708.html", FileMode.Create));
            document.Add(new Header(MarkupTags.HTML_ATTR_STYLESHEET, "myStyles.Css"));
            // step 3: we open the document
            document.Open();
            
            // step 4: we add content to the document
            Paragraph paragraph = new Paragraph("Please visit my ");
            Anchor anchor1 = new Anchor("website (external reference)");
            anchor1.Reference=("http://www.lowagie.Com/iText/");
            anchor1.Name=("top");
            paragraph.Add(anchor1);
            document.Add(paragraph);
            
            Paragraph entities = new Paragraph("These are some special characters: <, >, &, \" and '");
            document.Add(entities);
            
            document.Add(new Paragraph("some books I really like:"));
            List list;
            ListItem listItem;
            list = new List(true, 15);
            listItem = new ListItem("When Harlie was one");
            listItem.Add(new Chunk(" by David Gerrold"));
            list.Add(listItem);
            listItem = new ListItem("The World according to Garp");
            listItem.Add(new Chunk(" by John Irving"));
            list.Add(listItem);
            listItem = new ListItem("Decamerone");
            listItem.Add(new Chunk(" by Giovanni Boccaccio"));
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
			
            list.SetListSymbol("*");
            listItem = new ListItem("Isaac Asimov");
            listItem.SetMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS, "small");
            list.Add(listItem);
            List sublist;
            sublist = new List(true, 10);
            sublist.SetListSymbol("");
            sublist.Add("The Foundation Trilogy");
            sublist.Add("The Complete Robot");
            sublist.Add("Caves of Steel");
            sublist.Add("The Naked Sun");
            list.Add(sublist);
            listItem = new ListItem("John Irving");
            listItem.SetMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS, "small");
            list.Add(listItem);
            sublist = new List(true, 10);
            sublist.SetListSymbol("");
            sublist.Add("The World according to Garp");
            sublist.Add("Hotel New Hampshire");
            sublist.Add("A prayer for Owen Meany");
            sublist.Add("Widow for a year");
            list.Add(sublist);
            listItem = new ListItem("Kurt Vonnegut");
            listItem.SetMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS, "small");
            list.Add(listItem);
            sublist = new List(true, 10);
            sublist.SetListSymbol("");
            sublist.Add("Slaughterhouse 5");
            sublist.Add("Welcome to the Monkey House");
            sublist.Add("The great pianola");
            sublist.Add("Galapagos");
            list.Add(sublist);
            document.Add(list);
            
            paragraph = new Paragraph("\n\n");
            document.Add(paragraph);
            
            Table table = new Table(3);
            table.BorderWidth=(1);
            table.BorderColor=(new Color(0, 0, 255));
            table.Padding=(5);
            table.Spacing=(5);
            Cell cell = new Cell("header");
            cell.Header=(true);
            cell.Colspan=(3);
            table.AddCell(cell);
            table.EndHeaders();
            cell = new Cell("example cell with colspan 1 and rowspan 2");
            cell.Rowspan=(2);
            cell.BorderColor=(new Color(255, 0, 0));
            table.AddCell(cell);
            table.AddCell("1.1");
            table.AddCell("2.1");
            table.AddCell("1.2");
            table.AddCell("2.2");
            table.AddCell("cell test1");
            cell = new Cell("big cell");
            cell.Rowspan=(2);
            cell.Colspan=(2);
            table.AddCell(cell);
            table.AddCell("cell test2");
            document.Add(table);
            
            Image jpeg = Image.GetInstance("myKids.jpg");
            document.Add(jpeg);
            Image png = Image.GetInstance(new Uri("http://www.lowagie.Com/iText/examples/hitchcock.png"));
            document.Add(png);
            Anchor anchor2 = new Anchor("please jump to a local destination");
            anchor2.Reference=("#top");
            document.Add(anchor2);
            
            document.Add(paragraph);
            
            // we create some paragraphs
            Paragraph blahblah = new Paragraph("blah blah blah blah blah blah blaah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah");
            Paragraph blahblahblah = new Paragraph("blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blaah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah");
            
            // this loop will create 7 chapters
            for (int i = 1; i < 8; i++) {
                Paragraph cTitle = new Paragraph("This is chapter " + i);
                cTitle.SetMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS, "red");
                Chapter chapter = new Chapter(cTitle, i);
                
                if (i == 4) {
					blahblahblah.Alignment=Element.ALIGN_JUSTIFIED;
                    blahblah.Alignment=Element.ALIGN_JUSTIFIED;
                    chapter.Add(blahblah);
                }
                if (i == 5) {
                    blahblahblah.Alignment=(Element.ALIGN_CENTER);
                    blahblah.Alignment=(Element.ALIGN_RIGHT);
                    chapter.Add(blahblah);
                }
                // add a table in the 6th chapter
                if (i == 6) {
                    blahblah.Alignment=(Element.ALIGN_JUSTIFIED);
                    chapter.Add(table);
                }
                // in every chapter 3 sections will be added
                for (int j = 1; j < 4; j++) {
                    Paragraph sTitle = new Paragraph("This is section " + j + " in chapter " + i);
                    sTitle.SetMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS, "blue");
                    Section section = chapter.AddSection(sTitle, 1);
                    // in all chapters except the 1st one, some extra text is added to section 3
                    if (j == 3 && i > 1) {
                        section.Add(blahblah);
                    }
                    // in every section 3 subsections are added
                    for (int k = 1; k < 4; k++) {
                        Paragraph subTitle = new Paragraph("This is subsection " + k + " of section " + j);
                        subTitle.SetMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS, "gray");
                        Section subsection = section.AddSection(subTitle, 3);
                        if (k == 1 && j == 3) {
                            subsection.Add(blahblahblah);
                            subsection.Add(table);
                        }
                        subsection.Add(blahblah);
                    }
                    if (j == 2 && i > 2) {
                        section.Add(blahblahblah);
                        section.Add(table);
                    }
                }
                document.Add(chapter);
            }
            
        }
        catch(DocumentException de) {
			Console.Error.WriteLine(de.Message);
		}
        catch(IOException ioe) {
			Console.Error.WriteLine(ioe.Message);
		}
        
        // step 5: we close the document
        document.Close();
		}
	}
}
