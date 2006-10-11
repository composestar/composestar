using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap02
{
	/// <summary>
	/// Chap02_hyphenation 的摘要说明。
	/// </summary>
	public class Chap02_hyphenation
	{
		public Chap02_hyphenation()
		{
        Console.WriteLine("Chapter 2: hyphenation");
        
        // step 1: creation of a document-object
        Document document = new Document(PageSize.A4, 100, 300, 100, 100);
        try {
            // step 2: we create a writer that listens to the document
            PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap02_hyphenation.pdf",FileMode.Create));
            // step 3: we open the document
            document.Open();
            // step 4: we add some content
            String text = "It was the best of times, it was the worst of times, " + 
                "it was the age of wisdom, it was the age of foolishness, " +
                "it was the epoch of belief, it was the epoch of incredulity, " +
                "it was the season of Light, it was the season of Darkness, " +
                "it was the spring of hope, it was the winter of despair, " +
                "we had everything before us, we had nothing before us, " +
                "we were all going direct to Heaven, we were all going direct " +
                "the other way\u2014in short, the period was so far like the present " +
                "period, that some of its noisiest authorities insisted on its " +
                "being received, for good or for evil, in the superlative degree " +
                "of comparison only.";
            document.Add(new Paragraph("GB"));
            Chunk ckEN = new Chunk(text);
            HyphenationAuto autoEN = new HyphenationAuto("en", "GB", 2, 2);
            ckEN.SetHyphenation(autoEN);
            Paragraph pEN = new Paragraph(ckEN);
            pEN.SetAlignment(ElementTags.ALIGN_JUSTIFIED);
            document.Add(pEN);
            document.Add(new Paragraph("US"));
            Chunk ckUS = new Chunk(text);
            HyphenationAuto autoUS = new HyphenationAuto("en", "US", 2, 2);
            ckUS.SetHyphenation(autoUS);
            Paragraph pUS = new Paragraph(ckUS);
            pUS.SetAlignment(ElementTags.ALIGN_JUSTIFIED);
            document.Add(pUS);
        }
        catch(Exception de) {
           Console.Error.WriteLine(de.Message);
        }
        // step 5: we close the document
        document.Close();
		}
	}
}
