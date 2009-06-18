concern BookObserver
{
	filtermodule Observable
	{
		internals
			handler : observer.Subject;
		inputfilters
			obs : Dispatch = ( selector == ['attach', 'detach'] ) 
				{ target = handler; };
			changes : Meta(target = handler, selector = 'notify') = 
				( selector == ['addChapter', 'removeChapter'] )
				cor ( selector == ['addParagraph', 'removeParagraph'] )
				cor ( selector == ['addText', 'setText'] )
	}
	
	superimposition
	{
		selectors
			books = { C | isClassWithNameInList(C, ['bookshelf.books.Book',
				'bookshelf.books.Chapter', 'bookshelf.books.Paragraph']) };
		filtermodules
			books <- Observable;
	}
}
