concern CreateIterator {

	filtermodule CreatorIterator{
		
		internals
			ic : Composestar.Patterns.Iterator.CreatorIterator;

		inputfilters
			create : Dispatch = { ic.* }
	}
	
	superimposition{
		selectors
			aggregate = { C | classHasAnnotationWithName(C,'Composestar.Patterns.Iterator.Annotations.ConcreteAggregate') };
		filtermodules
			aggregate <- CreatorIterator;
	}
	implementation in JSharp by	CreatorIterator as "CreatorIterator.jsl"
	{

		package Composestar.Patterns.Iterator;

		import java.util.Iterator;

		public class CreatorIterator
		{
			public CreatorIterator(){}

			public ReverseIterator createReverseIterator() 
			{
				return new ReverseIterator((OpenList)((Object)this));
			}
		}
	}

}