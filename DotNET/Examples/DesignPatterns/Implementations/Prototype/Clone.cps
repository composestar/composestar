concern Clone
{
	filtermodule CloneStringA
	{
		internals
			cloner : Composestar.Patterns.Prototype.CloneString;

		inputfilters
			cloneOperation : Dispatch = { [*.cloneMe] cloner.cloneA}	
	}
	filtermodule CloneStringB
	{
		internals
			cloner : Composestar.Patterns.Prototype.CloneString;

		inputfilters
			cloneOperation : Dispatch = { [*.cloneMe] cloner.cloneB}	
	}
	superimposition
	{
		selectors
			prototypeA = { PrototypeA | isClassWithName(PrototypeA, 'Composestar.Patterns.Prototype.StringPrototypeA') };
			prototypeB = { PrototypeB | isClassWithName(PrototypeB, 'Composestar.Patterns.Prototype.StringPrototypeB') };
			
		filtermodules
			prototypeA <- CloneStringA;
			prototypeB <- CloneStringB;
	}
	implementation in JSharp by	CloneString as "CloneString.jsl"
	{
		package Composestar.Patterns.Prototype;

		public class CloneString
		{
			public CloneString(){}

			public StringPrototypeA cloneA()
			{
				StringPrototypeA a = (StringPrototypeA)((Object)this);
				return new StringPrototypeA(a.text);
			}

			public StringPrototypeB cloneB()
			{
				StringPrototypeB b = (StringPrototypeB)((Object)this);
				return new StringPrototypeB(b.text);
			}
		}
	}
}