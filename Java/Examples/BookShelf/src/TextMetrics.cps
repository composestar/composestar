concern TextMetrics
{
	filtermodule Counter
	{
		internals
			counter : textproc.TextMetrics;
		inputfilters
			counters : Dispatch = { <counter.*> counter.* }
	}
	
	superimposition
	{
		selectors
			getText = { C | isClassWithNameInList(C, ['bookshelf.books.Book',
				'bookshelf.books.Chapter', 'bookshelf.books.Paragraph']) };
		filtermodules
			getText <- Counter;
	}
}
