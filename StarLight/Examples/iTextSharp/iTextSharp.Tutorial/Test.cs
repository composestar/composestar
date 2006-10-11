using System;
using iTextSharp.tutorial.Chap01;
using iTextSharp.tutorial.Chap02;
using iTextSharp.tutorial.Chap03;
using iTextSharp.tutorial.Chap04;
using iTextSharp.tutorial.Chap05;
using iTextSharp.tutorial.Chap06;
using iTextSharp.tutorial.Chap07;
using iTextSharp.tutorial.Chap08;
using iTextSharp.tutorial.Chap09;
using iTextSharp.tutorial.Chap10;
using iTextSharp.tutorial.Chap11;
using iTextSharp.tutorial.Chap12;
using iTextSharp.tutorial.Chap13;

namespace iTextSharp.tutorial
{
	/// <summary>
	/// Test 的摘要说明。
	/// </summary>
	public class Test
	{
		[STAThread]
		static void Main() 		
		{
			new Chap0101();
			new Chap0102();
			new Chap0103();
			new Chap0104();
			new Chap0106();
			new Chap0107();
			new Chap0108();
			new Chap0109();
			new Chap0110();
			new Chap0111();
			new Chap0112();
			new Concat("Concat.pdf,Chap0101.pdf,Chap0102.pdf,Chap0103.pdf".Split(','));
			new Encrypt("Chap0101.pdf,Encrypt.pdf,123456".Split(','));
			new Handout("Chap0703.pdf,Handout.pdf,4".Split(','));
			new Split("Chap0703.pdf,Split1.pdf,Split2.pdf,4".Split(','));	
			new Chap02.Chap0201();		
			new Chap02.Chap0202();
			new Chap02.Chap0203();
			new Chap02.Chap0204();
			new Chap02.Chap0205();
			new Chap02.Chap0206();
			new Chap02.Chap0207();
			new Chap02.Chap0208().Chap0208_();
			new Chap02.Chap0209();
			new Chap0301();
			new Chap0302();
			new Chap0303();
			new Chap0304();
			new Chap0401();
			new Chap0402();
			new Chap0403();
			new Chap0404();
			new Chap0405();
			new Chap0501();
			new Chap0502();
			new Chap0503();
			new Chap0504();
			new Chap0505();
			new Chap0506();
			new Chap0507();
			new Chap0508();
			new Chap0509();
			new Chap0510();
			new Chap0511();
			new Chap0512();
			new Chap0513();
			new Chap0514();
			new Chap0515();
			new Chap0516();
			new Chap0517();
			new Chap0518();
            //new Chap0601();
            //new Chap0602();
            //new Chap0603();
            //new Chap0604();
            //new Chap0605();
            //new Chap0606();
            //new Chap0607();
            //new Chap0608();
            //new Chap0609();
            //new Chap0610();
            //new Chap0611();
            //new Chap0613();
            //new Chap0614();
            //new Chap0615();
            //new Chap0616();
            //new Chap0701();
            //new Chap0702();
            //new Chap0703();
            //new Chap0704();
            //new Chap0705();
            //new Chap0801();
            //new Chap0802();
            //new Chap0803();
            //new Chap0804();
            //new Chap0901();
			//new Chap0902();
//            new Chap0903();
//            new Chap0904();
//            new Chap0905();
//            new Chap0906();
//            new Chap0907();
//            new Chap1001();
//            new Chap1002();
//            new Chap1003();
//            new Chap1004();
//            new Chap1005();
//            new Chap1006();
//            new Chap1007();
//            new Chap1008();
//            new Chap1009();
//            new Chap1010();
//            new Chap1011();
//            new Chap1012();
//            new Chap1013();
//            new Chap1014();
//            new Chap1015();
//            new Chap1101();
//            new Chap1102();
//            new Chap1103();
//            new Chap1104();
//            new Chap1105();
//            new Chap1106();
//            new Chap1107();
//            new Chap1108();
//            new Chap1109();
//            new Chap1110();
//            new Chap1201();
//            new Chap1202();
//            //new added
//            new Chap1203();
//            new Cv();
//            new Chap02_hyphenation();
//            new Chap0201a();
//            new Chap0707();
//            new Chap0708();
//            new Chap09_fontfactory_styles();
//            //need "STSong-Light", "UniGB-UCS2-H",
//            //but itextsharp does not has them,
//            //if you want to test it,you need 
//            //add them to itextsharp as content by your self.
////			new Chap0909();
//            new Chap1111();
//            new Chap13_form();
//            new Chap13_pdfreader();
		}
	}
}
