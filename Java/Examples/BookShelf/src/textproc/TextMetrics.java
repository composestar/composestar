package textproc;

import java.util.List;

import bookshelf.books.Book;
import bookshelf.books.Chapter;
import bookshelf.books.Paragraph;

public class TextMetrics {
    public int countWords() {
	try {
	    Book b = (Book) (Object) this;
	    int res = 0;
	    for (Chapter c : (List<Chapter>) b.getChapters()) {
		res += c.countWords();
	    }
	    return res;
	} catch (ClassCastException e) {
	}
	try {
	    Chapter c = (Chapter) (Object) this;
	    int res = 0;
	    for (Paragraph p : (List<Paragraph>) c.getParagraphs()) {
		res += p.countWords();
	    }
	    return res;
	} catch (ClassCastException e) {
	}
	try {
	    Paragraph p = (Paragraph) (Object) this;
	    return p.getText().split("\\s+").length;
/*
	    int wordCounter = 0;
	    boolean prevWhitespace = true;
	    String line = p.getText();
	    for (int i = 0; i < line.length(); i++) {
		if (Character.isWhitespace(line.charAt(i))) {
		    if (!prevWhitespace) // If there are several whitespace
					    // chars, only count the first time
			wordCounter++; // We reached the end of a word,
					// increase the counter
		    prevWhitespace = true;
		} else {
		    prevWhitespace = false;
		}
	    }
	    if (!prevWhitespace) {// Unless the line ends with whitespace, we
				    // didn't count the last word yet..
		wordCounter++;
	    }
	    return wordCounter;*/
	} catch (ClassCastException e) {
	}
	return -1;
    }

    public int countSentences() {
	try {
	    Book b = (Book) (Object) this;
	    int res = 0;
	    for (Chapter c : (List<Chapter>) b.getChapters()) {
		res += c.countSentences();
	    }
	    return res;
	} catch (ClassCastException e) {
	}
	try {
	    Chapter c = (Chapter) (Object) this;
	    int res = 0;
	    for (Paragraph p : (List<Paragraph>) c.getParagraphs()) {
		res += p.countSentences();
	    }
	    return res;
	} catch (ClassCastException e) {
	}
	try {
	    Paragraph p = (Paragraph) (Object) this;
	    return p.getText().split("\\.").length;
	} catch (ClassCastException e) {
	}
	return -1;
    }
}
