using System;
using System.IO;
using System.util;

using iTextSharp.text;
using iTextSharp.text.pdf;
using iTextSharp.text.xml;
using System.Collections;
//using iTextSharp.tutorial.Base;

namespace iTextSharp.tutorial.Chap07
{
	public class Chap0705 
	{
    
		public  Chap0705() 
		{
        
			Console.WriteLine("Chapter 7 example 5: simple database example");
        
			int i = 0;
			try 
			{			
				StreamReader reader = new StreamReader(new FileStream("simpleDB0705.txt", FileMode.Open));
            
				String line;
				while ((line = reader.ReadLine()) != null) 
				{
					
					Hashtable tagmap = new Hashtable();
					
					StringTokenizer tokenizer = new StringTokenizer(line, "|");
                
					XmlPeer peer = new XmlPeer(ElementTags.ITEXT, "letter");
					tagmap.Add(peer.Alias, peer);
					
					if (tokenizer.HasMoreTokens()) 
					{
						peer = new XmlPeer(ElementTags.CHUNK, "givenname");
						peer.Content = tokenizer.NextToken();
						tagmap.Add(peer.Alias, peer);
                    
					}
					if (tokenizer.HasMoreTokens()) 
					{
						peer = new XmlPeer(ElementTags.CHUNK, "name");
						peer.Content = tokenizer.NextToken();
						tagmap.Add(peer.Alias, peer);
                    
					}
					if (tokenizer.HasMoreTokens()) 
					{
						peer = new XmlPeer(ElementTags.CHUNK, "mail");
						peer.Content = tokenizer.NextToken();
						tagmap.Add(peer.Alias, peer);
                    
					}
					if (tokenizer.HasMoreTokens()) 
					{
						peer = new XmlPeer(ElementTags.ANCHOR, "website");
						String reference = tokenizer.NextToken();
						peer.Content = reference;
						peer.AddValue(ElementTags.REFERENCE, reference);
						peer.AddValue(ElementTags.COLOR, "#0000FF");
						tagmap.Add(peer.Alias, peer);
                    
					}
                
					// step 1: creation of a document-object
					Document document = new Document(PageSize.A4, 80, 50, 30, 65);
                
					// step 2:
					// we create a writer that listens to the document
					PdfWriter.GetInstance(document, new FileStream("Chap0705_" + (++i) + ".pdf", FileMode.Create));
                
					// step 3: we parse the document
					XmlParser.Parse(document, "simpleLetter0705.xml", tagmap);
				}
			}
			catch(Exception e) 
			{
				Console.Error.WriteLine(e.Message);
				Console.Error.WriteLine(e.StackTrace);
			}
		}
	}
}