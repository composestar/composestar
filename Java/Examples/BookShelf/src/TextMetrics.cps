concern TextMetrics
{
	filtermodule Counter
	{
		internals
			counter : textproc.TextMetrics;
		inputfilters
			counters : Dispatch = ( selector $= counter ) { target = counter; }
	}
	
	superimposition
	{
		selectors
			countables = { C | isClassWithNameInList(C, ['bookshelf.books.Book',
				'bookshelf.books.Chapter', 'bookshelf.books.Paragraph']) };
		filtermodules
			countables <- Counter;
	}
}
