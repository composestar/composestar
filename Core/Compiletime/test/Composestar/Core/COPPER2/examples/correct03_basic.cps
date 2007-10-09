// A basic concern example, does not contain all variations
concern correct03_basic in Concern.Examples
{
	filtermodule FM1
	{
		inputfilters
			test : Dispatch = { <inner.*> inner.* }
	}
	
	superimposition
	{
		selectors
			sel = { C | isClassWithName(C, 'AClass') };
		filtermodules
			sel <- FM1;
	}
	
	implementation in CSharp by AClass as "AClass.cs"
	{
public class AClass
{}	
	}
}
