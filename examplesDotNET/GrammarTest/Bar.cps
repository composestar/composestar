concern Bar in GrammarTest
{
	implementation in MyFirstLanguage by Bar as "Bar.cs"
  {
    using System;
    
    namespace GrammarTest
    {
    
			public class Bar{
				public void write(){
					System.Console.WriteLine("Bar");
				}
				
				public bool isBar(){
					return true;
				}
			}
    }
  }
}