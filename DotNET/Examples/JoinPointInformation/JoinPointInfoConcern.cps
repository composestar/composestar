concern JoinPointInfoConcern(ext_foo : JPInfo.Foo ; ext_bar : JPInfo.Bar) in JPInfo
{
	filtermodule JPInfoModule
	{
		internals
			in_foo : JPInfo.Foo;
			in_bar : JPInfo.Bar;
			jpie   : JPInfo.JoinPointInfoExtractor;
		conditions
			cond : jpie.doCondition();
		inputfilters
			meta : Meta = { cond => [*.*] jpie.extract }
	}

	superimposition
	{
		filtermodules
			self <- JPInfoModule;
	}

	implementation in CSharp by JPInfo.JoinPointInfoConcern as "JoinPointInfoConcern.cs"
	{
		using System;

		namespace JPInfo
		{
			public class JoinPointInfoConcern
			{
				public JoinPointInfoConcern(Foo foo, Bar bar)
				{
				}

				public void sayHello()
				{
					Console.WriteLine("Hello");
				}
			}
		}
	}
}