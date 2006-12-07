concern Foo in GrammarTest
{
  filtermodule Foo{
		internals
			bar : GrammarTest.Bar;
		externals
		  outerfoo : GrammarTest.OuterFoo = GrammarTest.OuterFoo.instance();
		conditions
		  isBar : bar.isBar();
		inputfilters
		  foobar : Dispatch = {True & True =>[*.*] bar.*}
		outputfilters
		  barfoo : Dispatch (isBar, bar, foobar)= {}
  }
  
  filtermodule Filtermodule{
  
  }
  
  filtermodule Filtermodule2{
		internals
		externals
		conditions
  }
  
  superimposition{  
		selectors
		  sel = {C | isClassWithNameInList(C, ['GrammarTest.Foo', 'GrammarTest.OuterFoo'])};
		filtermodules
			self <- Foo;
			sel <- Filtermodule;
		annotations
		  sel <- GrammarTest.Translate;
		constraints
		  pre (Filtermodule, Filtermodule2);
  }
  
  implementation in CSharp by Foo as "Foo.cs"
  {
    using System;
    
    namespace GrammarTest
    {
    
			public class Foo{
				public void write(){
					System.Console.WriteLine("Foo");
				}
			}
    }
  }
}
