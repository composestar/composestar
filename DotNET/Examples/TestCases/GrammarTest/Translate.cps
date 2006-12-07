concern Translate in GrammarTest{
	filtermodule translate{
		inputfilters
		  translateFunctions : Dispatch = {[*.schrijf] *.write}
  }
  
  superimposition{
		selectors
		  sel = {C | classHasAnnotation(C, 'GrammarTest.Translate')};
		filtermodules
			sel <-  translate;
		constraints
		  pre (translate, Foo);
  }
  
  implementation in CSharp by Translate as "Translate.cs"
  {
		using System;
		namespace GrammarTest{
		
			public class Translate : System.Attribute
			{
			}
		} 
  }
}
