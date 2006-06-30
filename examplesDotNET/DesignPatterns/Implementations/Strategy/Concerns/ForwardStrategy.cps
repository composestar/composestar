concern ForwardStrategy
{
	filtermodule ForwardStrategy
	{
		internals
			bubble : Composestar.Patterns.Strategy.BubbleSort;
			linear : Composestar.Patterns.Strategy.LinearSort;
			quick  : Composestar.Patterns.Strategy.QuickSort;

		conditions
			ltten : inner.lessThanTen();
			lttwenty : inner.lessThanTwenty();

		inputfilters
			determineStrategy : Meta = {[*.sort] inner.determineStrategy };
			use_bubble : Dispatch = { ltten => bubble.* };
			use_linear : Dispatch = { lttwenty => linear.*};
			use_quick : Dispatch = { quick.*}
	}
	superimposition
	{
		selectors
			context = { Context | classHasAnnotationWithName(Context, 'Composestar.Patterns.Strategy.Annotations.Context') };
			
		filtermodules
			context <- ForwardStrategy;
	}
}