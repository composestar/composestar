package bookshelf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bookshelf.books.Book;

public class Shelf {
    protected List<Book> books;

    public Shelf() {
	books = new ArrayList<Book>();
    }

    public void addBook(Book newBook) {
	books.add(newBook);
    }

    public boolean removeBook(Book oldBook) {
	return books.remove(oldBook);
    }

    public List<Book> getBooks() {
	return Collections.unmodifiableList(books);
    }

    public Book getBook(int index) {
	return books.get(0);
    }
}
