package textproc;

import java.text.BreakIterator;
import java.util.List;

import observer.IObserver;

import bookshelf.books.Book;
import bookshelf.books.Chapter;
import bookshelf.books.Paragraph;

public class TextMetrics implements IObserver {
    protected TextAlgorithms ta;
    protected boolean init = true;
    protected Book book;
    protected Chapter chapter;
    protected Paragraph paragraph;

    public TextMetrics() {
	ta = new TextAlgorithms();
    }

    public void internalInitialize() {
	book = internalGetBook();
	if (book != null) {
	    book.attach(this);
	}
	chapter = internalGetChapter();
	paragraph = internalGetParagraph();
	init = false;
    }

    public Book internalGetBook() {
	try {
	    Book b = (Book) (Object) this;
	    return b;
	} catch (ClassCastException e) {
	}
	return null;
    }

    public Chapter internalGetChapter() {
	try {
	    Chapter c = (Chapter) (Object) this;
	    return c;
	} catch (ClassCastException e) {
	}
	return null;
    }

    public Paragraph internalGetParagraph() {
	try {
	    Paragraph p = (Paragraph) (Object) this;
	    return p;
	} catch (ClassCastException e) {
	}
	return null;
    }

    @Override
    public void subjectChanged(Object subject) {
	System.out.println("@@@@");
    }

    public int countWords() {
	if (init)
	    internalInitialize();
	if (book != null) {
	    int res = 0;
	    for (Chapter c : (List<Chapter>) book.getChapters()) {
		res += c.countWords();
	    }
	    return res;
	} else if (chapter != null) {
	    int res = 0;
	    for (Paragraph p : (List<Paragraph>) chapter.getParagraphs()) {
		res += p.countWords();
	    }
	    return res;
	} else if (paragraph != null) {
	    return ta.numberOfWords(paragraph.getText());
	}
	return -1;
    }

    public int countSentences() {
	if (init)
	    internalInitialize();
	if (book != null) {
	    int res = 0;
	    for (Chapter c : (List<Chapter>) book.getChapters()) {
		res += c.countSentences();
	    }
	    return res;
	} else if (chapter != null) {
	    int res = 0;
	    for (Paragraph p : (List<Paragraph>) chapter.getParagraphs()) {
		res += p.countSentences();
	    }
	    return res;
	} else if (paragraph != null) {
	    return ta.numberOfSentences(paragraph.getText());
	}
	return -1;
    }

    public static class TextAlgorithms {
	BreakIterator wordCounter = BreakIterator.getWordInstance();
	BreakIterator sentenceCounter = BreakIterator.getSentenceInstance();

	public TextAlgorithms() {
	};

	public int numberOfWords(String subject) {
	    wordCounter.setText(subject);
	    int cnt = 0;
	    int pos = wordCounter.first();
	    while (pos != BreakIterator.DONE) {
		cnt++;
		pos = wordCounter.next();
	    }
	    return cnt;
	}

	public int numberOfSentences(String subject) {
	    sentenceCounter.setText(subject);
	    int cnt = 0;
	    int pos = sentenceCounter.first();
	    while (pos != BreakIterator.DONE) {
		cnt++;
		pos = sentenceCounter.next();
	    }
	    return cnt;
	}
    }
}
