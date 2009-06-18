concern LogActions
{
	filtermodule LogTextMetrics
	{
		inputfilters
			logcnt : Log = ( selector == 'countWords' 
				| selector == 'countSentences' )
	}
	
	superimposition
	{
		selectors
			bookparts = { C | isClassWithNameInList(C, ['bookshelf.books.Book',
				'bookshelf.books.Chapter', 'bookshelf.books.Paragraph']) };
		filtermodules
			bookparts <- LogTextMetrics;
		constraints
			//pre(TextMetrics.Counter, LogActions.LogTextMetrics);
			pre(LogActions.LogTextMetrics, TextMetrics.Counter);
	}
}