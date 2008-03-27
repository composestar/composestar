package textproc;

import java.lang.ref.WeakReference;
import java.text.BreakIterator;

import observer.IObserver;
import bookshelf.books.Book;
import bookshelf.books.Chapter;
import bookshelf.books.Paragraph;

public class TextMetrics implements IObserver {
    protected WeakReference<Book> book;
    protected WeakReference<Chapter> chapter;
    protected WeakReference<Paragraph> paragraph;

    public int countWords() {
	if (init) {
	    internalInitialize();
	}
	if (book != null && book.get() != null) {
	    int res = 0;
	    for (Chapter c : book.get().getChapters()) {
		res += c.countWords();
	    }
	    return res;
	} else if (chapter != null && chapter.get() != null) {
	    int res = 0;
	    for (Paragraph p : chapter.get().getParagraphs()) {
		res += p.countWords();
	    }
	    return res;
	} else if (paragraph != null && paragraph.get() != null) {
	    return ta.numberOfWords(paragraph.get().getText());
	}
	return -1;
    }

    public int countSentences() {
	if (init) {
	    internalInitialize();
	}
	if (book != null && book.get() != null) {
	    int res = 0;
	    for (Chapter c : book.get().getChapters()) {
		res += c.countSentences();
	    }
	    return res;
	} else if (chapter != null && chapter.get() != null) {
	    int res = 0;
	    for (Paragraph p : chapter.get().getParagraphs()) {
		res += p.countSentences();
	    }
	    return res;
	} else if (paragraph != null && paragraph.get() != null) {
	    return ta.numberOfSentences(paragraph.get().getText());
	}
	return -1;
    }

    protected TextAlgorithms ta;
    protected boolean init = true;

    public TextMetrics() {
	ta = new TextAlgorithms();
    }

    public void internalInitialize() {
	book = internalGetBook();
	if (book != null) {
	    book.get().attach(this);
	}
	if (book == null) {
	    chapter = internalGetChapter();
	    if (chapter != null) {
		chapter.get().attach(this);
	    }
	}
	if (book == null && chapter == null) {
	    paragraph = internalGetParagraph();
	    if (paragraph != null) {
		paragraph.get().attach(this);
	    }
	}
	init = false;
    }

    public WeakReference<Book> internalGetBook() {
	try {
	    Book b = (Book) (Object) this;
	    return new WeakReference<Book>(b);
	} catch (ClassCastException e) {
	}
	return null;
    }

    public WeakReference<Chapter> internalGetChapter() {
	try {
	    Chapter c = (Chapter) (Object) this;
	    return new WeakReference<Chapter>(c);
	} catch (ClassCastException e) {
	}
	return null;
    }

    public WeakReference<Paragraph> internalGetParagraph() {
	try {
	    Paragraph p = (Paragraph) (Object) this;
	    return new WeakReference<Paragraph>(p);
	} catch (ClassCastException e) {
	}
	return null;
    }

    public void bookUpdated() {
    }

    public void chapterUpdated() {
	chapter.get().withinBook().bookUpdated();
    }

    public void paragraphUpdated() {
	paragraph.get().withinChapter().chapterUpdated();
    }

    @Override
    public void subjectChanged(Object subject) {
	if (init) {
	    internalInitialize();
	}
	// this construction is used because "subjectChanged" isn't
	// captured by the runtime (TODO: why not?)
	if (book != null && subject == book.get()) {
	    bookUpdated();
	} else if (chapter != null && subject == chapter.get()) {
	    chapterUpdated();
	} else if (paragraph != null && subject == paragraph.get()) {
	    paragraphUpdated();
	}
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
