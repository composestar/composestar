concern ReusedFoo (ext_foo : GrammarTest.Foo) in GrammarTest{
	  
 	implementation in CSharp by GrammarTest.ReusedFoo as "ReusedFoo.cs"{
		namespace GrammarTest{
			public class ReusedFoo {
				public ReusedFoo(Foo foo){ }
			}	
		}
	}
}