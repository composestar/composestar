using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap10
{
	public class Chap1008 
	{
    
		public Chap1008() 
		{
        
			Console.WriteLine("Chapter 10 example 8: Irregular columns");
        
			// step 1: creation of a document-object
			Document document = new Document();
        
			try 
			{
				// step 2: creation of the writer
				PdfWriter writer = PdfWriter.GetInstance(document, new FileStream("Chap1008.pdf", FileMode.Create));
            
				// step 3: we open the document
				document.Open();
            
				// step 4:
				BaseFont bf = BaseFont.CreateFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				Font font = new Font(bf, 11, Font.NORMAL);
            
				// we grab the contentbyte and do some stuff with it
				PdfContentByte cb = writer.DirectContent;
            
				PdfTemplate t = cb.CreateTemplate(600, 800);
				Image caesar = Image.GetInstance("caesar_coin.jpg");
				cb.AddImage(caesar, 100, 0, 0, 100, 260, 595);
				t.SetGrayFill(0.75f);
				t.MoveTo(310, 112);
				t.LineTo(280, 60);
				t.LineTo(340, 60);
				t.ClosePath();
				t.MoveTo(310, 790);
				t.LineTo(310, 710);
				t.MoveTo(310, 580);
				t.LineTo(310, 122);
				t.Stroke();
				cb.AddTemplate(t, 0, 0);
            
				ColumnText ct = new ColumnText(cb);
				ct.AddText(new Phrase("GALLIA est omnis divisa in partes tres, quarum unam incolunt Belgae, aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli appellantur.  Hi omnes lingua, institutis, legibus inter se differunt. Gallos ab Aquitanis Garumna flumen, a Belgis Matrona et Sequana dividit. Horum omnium fortissimi sunt Belgae, propterea quod a cultu atque humanitate provinciae longissime absunt, minimeque ad eos mercatores saepe commeant atque ea quae ad effeminandos animos pertinent important, proximique sunt Germanis, qui trans Rhenum incolunt, quibuscum continenter bellum gerunt.  Qua de causa Helvetii quoque reliquos Gallos virtute praecedunt, quod fere cotidianis proeliis cum Germanis contendunt, cum aut suis finibus eos prohibent aut ipsi in eorum finibus bellum gerunt.\n", FontFactory.GetFont(FontFactory.HELVETICA, 12)));
				ct.AddText(new Phrase("[Eorum una, pars, quam Gallos obtinere dictum est, initium capit a flumine Rhodano, continetur Garumna flumine, Oceano, finibus Belgarum, attingit etiam ab Sequanis et Helvetiis flumen Rhenum, vergit ad septentriones. Belgae ab extremis Galliae finibus oriuntur, pertinent ad inferiorem partem fluminis Rheni, spectant in septentrionem et orientem solem. Aquitania a Garumna flumine ad Pyrenaeos montes et eam partem Oceani quae est ad Hispaniam pertinet; spectat inter occasum solis et septentriones.]\n", FontFactory.GetFont(FontFactory.HELVETICA, 12)));
				ct.AddText(new Phrase("Apud Helvetios longe nobilissimus fuit et ditissimus Orgetorix.  Is M. Messala, [et P.] M.  Pisone consulibus regni cupiditate inductus coniurationem nobilitatis fecit et civitati persuasit ut de finibus suis cum omnibus copiis exirent:  perfacile esse, cum virtute omnibus praestarent, totius Galliae imperio potiri.  Id hoc facilius iis persuasit, quod undique loci natura Helvetii continentur:  una ex parte flumine Rheno latissimo atque altissimo, qui agrum Helvetium a Germanis dividit; altera ex parte monte Iura altissimo, qui est inter Sequanos et Helvetios; tertia lacu Lemanno et flumine Rhodano, qui provinciam nostram ab Helvetiis dividit.  His rebus fiebat ut et minus late vagarentur et minus facile finitimis bellum inferre possent; qua ex parte homines bellandi cupidi magno dolore adficiebantur.  Pro multitudine autem hominum et pro gloria belli atque fortitudinis angustos se fines habere arbitrabantur, qui in longitudinem milia passuum CCXL, in latitudinem CLXXX patebant.\n", FontFactory.GetFont(FontFactory.HELVETICA, 12)));
				ct.AddText(new Phrase("His rebus adducti et auctoritate Orgetorigis permoti constituerunt ea quae ad proficiscendum pertinerent comparare, iumentorum et carrorum quam maximum numerum coemere, sementes quam maximas facere, ut in itinere copia frumenti suppeteret, cum proximis civitatibus pacem et amicitiam confirmare.  Ad eas res conficiendas biennium sibi satis esse duxerunt; in tertium annum profectionem lege confirmant.  Ad eas res conficiendas Orgetorix deligitur.  Is sibi legationem ad civitates suscipit.  In eo itinere persuadet Castico, Catamantaloedis filio, Sequano, cuius pater regnum in Sequanis multos annos obtinuerat et a senatu populi Romani amicus appellatus erat, ut regnum in civitate sua occuparet, quod pater ante habuerit; itemque Dumnorigi Haeduo, fratri Diviciaci, qui eo tempore principatum in civitate obtinebat ac maxime plebi acceptus erat, ut idem conaretur persuadet eique filiam suam in matrimonium dat.  Perfacile factu esse illis probat conata perficere, propterea quod ipse suae civitatis imperium obtenturus esset:  non esse dubium quin totius Galliae plurimum Helvetii possent; se suis copiis suoque exercitu illis regna conciliaturum confirmat.  Hac oratione adducti inter se fidem et ius iurandum dant et regno occupato per tres potentissimos ac firmissimos populos totius Galliae sese potiri posse sperant.\n", FontFactory.GetFont(FontFactory.HELVETICA, 12)));
				ct.AddText(new Phrase("Ea res est Helvetiis per indicium enuntiata.  Moribus suis Orgetoricem ex vinculis causam dicere coegerunt; damnatum poenam sequi oportebat, ut igni cremaretur.  Die constituta causae dictionis Orgetorix ad iudicium omnem suam familiam, ad hominum milia decem, undique coegit, et omnes clientes obaeratosque suos, quorum magnum numerum habebat, eodem conduxit; per eos ne causam diceret se eripuit.  Cum civitas ob eam rem incitata armis ius suum exequi conaretur multitudinemque hominum ex agris magistratus cogerent, Orgetorix mortuus est; neque abest suspicio, ut Helvetii arbitrantur, quin ipse sibi mortem consciverit.", FontFactory.GetFont(FontFactory.HELVETICA, 12)));
            
				float[] left1  = {70,790, 70,60};
				float[] right1 = {300,790, 300,700, 240,700, 240,590, 300,590, 300,106, 270,60};
				float[] left2  = {320,790, 320,700, 380,700, 380,590, 320,590, 320,106, 350,60};
				float[] right2 = {550,790, 550,60};
            
				int status = 0;
				int column = 0;
				while ((status & ColumnText.NO_MORE_TEXT) == 0) 
				{
					if (column == 0) 
					{
						ct.SetColumns(left1, right1);
						column = 1;
					}
					else 
					{
						ct.SetColumns(left2, right2);
						column = 0;
					}
					status = ct.Go();
					ct.YLine = 790;
					ct.Alignment = Element.ALIGN_JUSTIFIED;
					status = ct.Go();
					if ((column == 0) && ((status & ColumnText.NO_MORE_COLUMN) != 0)) 
					{
						document.NewPage();
						cb.AddTemplate(t, 0, 0);
						cb.AddImage(caesar, 100, 0, 0, 100, 260, 595);
					}
				}
            
				// step 5: we close the document
				document.Close();
			}
			catch(DocumentException de) 
			{
				Console.Error.WriteLine(de.Message);
			}
			catch(IOException ioe) 
			{
				Console.Error.WriteLine(ioe.Message);
			}
		}
	}
}