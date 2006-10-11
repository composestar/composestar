using System;
using System.IO;

using iTextSharp.text;
using iTextSharp.text.pdf;

namespace iTextSharp.tutorial.Chap13
{
	/// <summary>
	/// Chap13_form 的摘要说明。
	/// </summary>
	public class Chap13_form
	{
		public Chap13_form()
		{
       Console.WriteLine("Chapter 13: a form with some text");

        // step 1: creation of a document-object
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        try {
            // step 2: we create a writer that listens to the document
            PdfWriter writer = PdfWriter.GetInstance(document,  new FileStream("Chap13_form.pdf", FileMode.Create));
            PdfAcroForm acroForm = writer.AcroForm;
            // step 3: we open the document
            document.Open();
            // step 4: we add some content
            BaseFont helv = BaseFont.CreateFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            float fontSize = 12;
            acroForm.AddSingleLineTextField("name", "your name", helv, fontSize, 171, 800, 350, 820);
            acroForm.AddMultiLineTextField("msg", "Hello iText!\nThis is a Test\nThere are multiple lines in this textfield", helv, fontSize, 171, 730, 350, 790);
            acroForm.AddSingleLinePasswordField("password", "", helv, fontSize, 171, 700, 350, 720);
            acroForm.AddHtmlPostButton("btn", "SUBMIT", "noValue", "Chap13.php", helv, fontSize, 355, 700, 420, 725);
            acroForm.AddResetButton("reset", "RESET", null, helv, fontSize, 430, 700, 495, 725);
            acroForm.AddCheckBox("CB1", "Food", true, 180, 685, 190, 695);
            acroForm.AddCheckBox("CB2", "Drinks", false, 180, 645, 210, 675);
            PdfFormField radiogroup = acroForm.GetRadioGroup("CreditCard", "Visa", true);
            acroForm.AddRadioButton(radiogroup, "Visa", 180, 625, 195, 640);
            acroForm.AddRadioButton(radiogroup, "MasterCard", 200, 625, 215, 640);
            acroForm.AddRadioButton(radiogroup, "American", 220, 625, 235, 640);
            acroForm.AddRadioGroup(radiogroup);
            String[] colors = {"Red", "Green", "Blue"};
            String[,] colorvalues = {{"#FF0000", "Red"}, {"#00FF00", "Green"}, {"#0000FF", "Blue"}};
            acroForm.AddSelectList("list1", colors, "Green", helv, fontSize, 171, 550, 300, 600);
            acroForm.AddSelectList("list2", colorvalues, "#0000FF", helv, fontSize, 315, 550, 450, 600);
            acroForm.AddComboBox("combo1", colors, "Green", true, helv, fontSize, 171, 440, 300, 490);
            acroForm.AddComboBox("combo2", colorvalues, "#0000FE", false, helv, fontSize, 315, 440, 450, 490);
            PdfContentByte cb = new PdfContentByte(null);
            cb.SetRGBColorStroke(0x00, 0x00, 0x00);
            cb.SetRGBColorFill(0xFF, 0x00, 0x00);
            cb.Arc(0, 0, 100, 100, 0, 360);
            cb.FillStroke();
            cb.SetRGBColorStroke(0x00, 0x00, 0x00);
            cb.SetRGBColorFill(0xFF, 0xFF, 0xFF);
            cb.Arc(20, 20, 80, 80, 0, 360);
            cb.FillStroke();
            cb.SetRGBColorStroke(0x00, 0x00, 0x00);
            cb.SetRGBColorFill(0xFF, 0x00, 0x00);
            cb.Arc(40, 40, 60, 60, 0, 360);
            cb.FillStroke();
            cb.SetRGBColorFill(0x00, 0x00, 0x00);
            cb.Arc(48, 48, 52, 52, 0, 360);
            cb.Fill();
            acroForm.AddMap("map", null, "Chap13.php", cb, 171, 300, 271, 400);
            acroForm.AddHiddenField("hidden", "secret");
        }
		catch(DocumentException de) 
		{
			Console.Error.WriteLine(de.Message);
		}
		catch(IOException ioe) 
		{
			Console.Error.WriteLine(ioe.Message);
		}

        // step 5: close the document
        document.Close();
		}
	}
}
