using System;
using System.IO;
using System.Collections;
using System.util;

using iTextSharp.text;
using iTextSharp.text.pdf;
using iTextSharp.text.markup;
using iTextSharp.text.xml;

namespace iTextSharp.tutorial.Chap12
{

	/**
	 * We want to change the behaviour of the handler in some cases
	 */

	class MyHandler : ITextmyHandler 
	{
    
		/**
		 * We have to override the constructor
		 */
    
		public MyHandler(Document document, Hashtable tagmap) : base(document, tagmap) {}
    
		/**
		 * We only alter the handling of some endtags.
		 */
    
		public override void EndElement(String uri, String lname, String name) 
		{
			if (myTags.ContainsKey(name)) 
			{
				XmlPeer peer = (XmlPeer) myTags[name];
				// we don't want the document to be close
				// because we are going to add a page after the xml is parsed
				
				if (IsDocumentRoot(peer.Tag)) 
				{
					return;
				}
				HandleEndingTags(peer.Tag);
				// we want to add a paragraph after the speaker chunk
				if ("SPEAKER".Equals(name)) 
				{
					try 
					{
						ITextElementArray previous = (ITextElementArray) stack.Pop();
						previous.Add(new Paragraph(16));
						stack.Push(previous);
					}
					catch 
					{
					}
				}
			}
			else 
			{
				HandleEndingTags(name);
			}
		}
	}

	/**
	 * We use the tagfile from chapter 7, but we want to change some tag definitions.
	 */

	class MyMap : TagMap 
	{
    
		public MyMap(String tagfile) : base(tagfile) 
		{
			XmlPeer peer = new XmlPeer(MarkupTags.CSS_KEY_FONTSIZE, "10");
			//new XmlPeer(ElementTags.CHUNK, "SPEAKER");÷ÿ∏¥º”‘ÿSPEAKER
			//peer.AddValue(MarkupTags.CSS_KEY_FONTSIZE, "10");
			peer.AddValue(MarkupTags.CSS_KEY_FONTWEIGHT, MarkupTags.CSS_VALUE_BOLD);
			peer.AddValue(ElementTags.GENERICTAG, "");
			Add(peer.Alias, peer);
		}
	}

	/**
	 * This object contains a speaker and a number of occurrances in the play
	 */

	class Speaker 
	{
    
		// name of the speaker
		private String name;
    
		// number of occurrances
		private int occurrance = 1;
    
		public Speaker(String name) 
		{
			this.name = name;
		}
    
		public String Name 
		{
			get { return name; }
		}
    
		public int Occurrance 
		{
			get { return occurrance; }
			set { occurrance = value; }
		}    
	}

	/**
	 * Your own page events.
	 */

	class MyPageEvents : PdfPageEventHelper 
	{
    
		// we will keep a list of speakers
		SortedList speakers = new SortedList();
    
		// This is the contentbyte object of the writer
		PdfContentByte cb;
    
		// we will put the final number of pages in a template
		PdfTemplate template;
    
		// this is the BaseFont we are going to use for the header / footer
		BaseFont bf = null;
    
		// this is the current act of the play
		String act = "";
    
		// we override the onGenericTag method
		public override void OnGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) 
		{
			if (speakers.Contains(text)) 
			{
				((Speaker)speakers[text]).Occurrance++;
			} 
			else 
			{
				speakers.Add(text, new Speaker(text));
			}
		}
    
		// we override the onOpenDocument method
		public override void OnOpenDocument(PdfWriter writer, Document document) 
		{
			try 
			{
				bf = BaseFont.CreateFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				cb = writer.DirectContent;
				template = cb.CreateTemplate(50, 50);
			}
			catch(DocumentException de) 
			{
			}
			catch(IOException ioe) 
			{
			}
		}
    
		// we override the onChapter method
		public override void OnChapter(PdfWriter writer,Document document,float paragraphPosition,Paragraph title) 
		{
			System.Text.StringBuilder buf = new System.Text.StringBuilder();
			foreach (Chunk chunk in title.Chunks) 
			{
				buf.Append(chunk.Content);
			}
			act = buf.ToString();
		}
    
		// we override the onEndPage method
		public override void OnEndPage(PdfWriter writer, Document document) 
		{
			int pageN = writer.PageNumber;
			String text = "Page " + pageN + " of ";
			float len = bf.GetWidthPoint(text, 8);
			cb.BeginText();
			cb.SetFontAndSize(bf, 8);
			cb.SetTextMatrix(280, 30);
			cb.ShowText(text);
			cb.EndText();
			cb.AddTemplate(template, 280 + len, 30);
			cb.BeginText();
			cb.SetFontAndSize(bf, 8);
			cb.SetTextMatrix(280, 820);
			if (pageN % 2 == 1) 
			{
				cb.ShowText("Romeo and Juliet");
			}
			else 
			{
				cb.ShowText(act);
			}
			cb.EndText();
		}
    
		// we override the onCloseDocument method
		public override void OnCloseDocument(PdfWriter writer, Document document) 
		{
			template.BeginText();
			template.SetFontAndSize(bf, 8);
			template.ShowText((writer.PageNumber - 1).ToString());
			template.EndText();
		}
    
		// we add a method to retrieve the glossary
		public SortedList Speakers 
		{
			get 
			{
				return speakers;
			}
		}
	}

	public class Chap1201 
	{
    
		public Chap1201() 
		{
        
			Console.WriteLine("Chapter 12 example 1: page events");
        
			// step 1: creation of a document-object
			Document document = new Document(PageSize.A4, 80, 50, 30, 65);
        
			try 
			{
				// step 2:
				// we create a writer that listens to the document
				// and directs a XML-stream to a file
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1201.pdf", FileMode.Create));
            
				// create add the event handler
				MyPageEvents events = new MyPageEvents();
				writer.PageEvent = events;
            
				// step 3: we create a parser and set the document handler
				MyHandler h = new MyHandler(document, new MyMap("tagmap0703.xml"));
            
				// step 4: we parse the document
				h.Parse("Chap0703.xml");
            
				document.NewPage();
				foreach (string key in events.Speakers.Keys) 
				{
					Speaker speaker = (Speaker)events.Speakers[key];
					document.Add(new Paragraph(speaker.Name + ": " + speaker.Occurrance + " speech blocks"));
				}
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