concern CompositePattern
{
	filtermodule Leaf
	{
		internals
			leaf : Composestar.Patterns.Composite.Leaf;

		inputfilters
			l : Dispatch = { leaf.* }
	}
	filtermodule Composite
	{
		internals
			composite : Composestar.Patterns.Composite.Composite;

		inputfilters
			c : Dispatch = { composite.* }
	}
	superimposition
	{
		selectors
			composite = { Adaptee | classHasAnnotationWithName(Adaptee, 'Composestar.Patterns.Composite.Annotations.Composite') };
			leaf = { Leaf | classHasAnnotationWithName(Leaf , 'Composestar.Patterns.Composite.Annotations.Leaf') };

		filtermodules
			leaf <- Leaf;
			composite <- Composite;
	}
}