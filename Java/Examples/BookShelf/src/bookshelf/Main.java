package bookshelf;

import java.lang.ref.WeakReference;

import observer.ObserveBookSize;
import bookshelf.books.Book;
import bookshelf.books.Chapter;
import bookshelf.generators.ArmyOfMonkeys;

public class Main {

    Shelf shelf;

    public Main() {
	shelf = new Shelf();
    }

    public void createBooks(int booksToWrite) {
	System.out.print(String.format("Writing %d books ", booksToWrite));
	for (int i = 0; i < booksToWrite; i++) {
	    Book book = ArmyOfMonkeys.writeBook();
	    shelf.addBook(book);
	    System.out.print(".");
	    System.out.flush();
	}
	System.out.println();
	System.out.println(String.format("There are %d books on the shelf",
		shelf.getBooks().size()));
    }

    /**
     * Calculate some metrics
     */
    public void shelfMetrics() {
	int cnt = 0;
	int mostWords = 0;
	int leastWords = Integer.MAX_VALUE;
	Book largest = null;
	Book smallest = null;

	System.out.print(String.format("Searching the largest book ", cnt));
	System.out.flush();

	for (Book book : shelf.getBooks()) {
	    System.out.print(".");
	    System.out.flush();
	    int cur = book.countWords();
	    cnt += cur;
	    if (cur > mostWords) {
		largest = book;
		mostWords = cur;
	    }
	    if (cur < leastWords) {
		smallest = book;
		leastWords = cur;
	    }
	}
	System.out.println();
	System.out.println(String.format("All books in total contain %d words",
		cnt));

	if (largest == null) {
	    return;
	}

	System.out.println(String.format(
		"The largest book is \"%s\" by \"%s\" (%d words)", largest
			.getTitle(), largest.getAuthor(), mostWords));
	System.out.println(String.format(
		"Its first paragraph contains %d words and %d sentences",
		largest.getChapter(0).getParagraph(0).countWords(), largest
			.getChapter(0).getParagraph(0).countSentences()));

	// Count books which are 33% smaller
	System.out.print(String.format("Counting books 33%% smaller ", cnt));
	System.out.flush();

	cnt = 0;
	for (Book book : shelf.getBooks()) {
	    System.out.print(".");
	    System.out.flush();
	    if (book == largest) {
		continue;
	    }
	    if (book.countWords() < mostWords * 0.66) {
		cnt++;
	    }
	}
	System.out.println();
	System.out.println(String.format(
		"%d books are 33%% smaller than the largest book", cnt));
    }

    public void doModification() {
	Book b = shelf.getBook(0);
	b.attach(new ObserveBookSize());
	Chapter newChap = ArmyOfMonkeys.writeChapter();
	b.addChapter(newChap);
	b.removeChapter(newChap);
    }

    public static void main(String[] args) {
	long time = System.currentTimeMillis();
	Main m = new Main();
	m.createBooks(10);
	m.shelfMetrics();
	m.doModification();
	System.out.println(String.format("Time taken %dms", System
		.currentTimeMillis()
		- time));
    }
}
