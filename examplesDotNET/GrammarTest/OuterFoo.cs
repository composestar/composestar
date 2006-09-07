using System;

namespace GrammarTest
{
	/// <summary>
	/// Summary description for OuterFoo.
	/// </summary>
	[GrammarTest.Translate]
	public class OuterFoo
	{
	  private static OuterFoo theOuterFoo = null;
	  
		private OuterFoo(){}
		
		public static OuterFoo instance(){
			if(theOuterFoo == null){
				theOuterFoo = new OuterFoo();
			}
			
			return theOuterFoo;
		}
		
		public void write()
		{
			System.Console.WriteLine("OuterFoo"); // and no Öuterföö
		}
	}
}
