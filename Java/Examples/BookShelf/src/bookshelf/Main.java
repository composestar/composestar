package bookshelf;

import java.util.List;

import bookshelf.books.Book;
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
	
	System.out.print(String.format("Searching the largest book ",
		cnt));
	System.out.flush();
	
	for (Book b : (List<Book>) shelf.getBooks()) {
	    System.out.print(".");
	    System.out.flush();
	    int cur = b.countWords();
	    cnt += cur;
	    if (cur > mostWords) {
		largest = b;
		mostWords = cur;
	    }
	    if (cur < leastWords) {
		smallest = b;
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
	System.out.print(String.format("Counting books 33%% smaller ",
		cnt));
	System.out.flush();
	
	cnt = 0;
	for (Book b : (List<Book>) shelf.getBooks()) {
	    System.out.print(".");
	    System.out.flush();
	    if (b == largest) {
		continue;
	    }
	    if (b.countWords() < mostWords * 0.66) {
		cnt++;
	    }
	}
	System.out.println();
	System.out.println(String.format(
		"%d books are 33%% smaller than the largest book", cnt));
    }

    public static void main(String[] args) {
	long time = System.currentTimeMillis();
	Main m = new Main();
	m.createBooks(10);
	m.shelfMetrics();
	System.out.println(String.format("Time taken %dms", System.currentTimeMillis()-time));
    }
}
