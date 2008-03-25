package observer;

import bookshelf.books.Book;

public class ObserveBookSize implements IObserver {

    @Override
    public void subjectChanged(Object subject) {
	Book b = (Book) subject;
	System.out.println(String.format(
		"Book \"%s\" changed. New size is: %d", b.getTitle(), b
			.countWords()));
    }

}
