concern BookObserver
{
	filtermodule Observable
	{
		internals
			handler : observer.Subject;
		inputfilters
			obs : Dispatch = { {[*.attach], [*.detach]} handler.* };
			changes : Meta = { {[*.addChapter], [*.removeChapter]} 
								handler.notify 
							 }
	}
	
	superimposition
	{
		selectors
			books = { C | isClassWithName(C, 'bookshelf.books.Book') };
		filtermodules
			books <- Observable;
	}
}
