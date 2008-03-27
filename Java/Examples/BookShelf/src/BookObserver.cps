concern BookObserver
{
	filtermodule Observable
	{
		internals
			handler : observer.Subject;
		inputfilters
			obs : Dispatch = { {[*.attach], [*.detach]} handler.* };
			bookChanges : Meta = { {[*.addChapter], [*.removeChapter]} 
								handler.notify };
			chapChanges : Meta = { {[*.addParagraph], [*.removeParagraph]} 
								handler.notify };
			paraChanges : Meta = { {[*.addText], [*.setText]} 
								handler.notify }
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
