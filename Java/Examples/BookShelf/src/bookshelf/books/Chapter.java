package bookshelf.books;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chapter {
    protected Book book;
    protected String title;
    protected List<Paragraph> paragraphs;

    public Chapter() {
	this("Untitled");
    }

    public Chapter(String chapTitle) {
	title = chapTitle;
	paragraphs = new ArrayList<Paragraph>();
    }

    public void setBook(Book inBook) {
	book = inBook;
    }

    public Book withinBook() {
	return book;
    }

    public void addParagraph(Paragraph p) {
	Chapter pc = p.withinChapter();
	if (pc != null && pc != this) {
	    pc.removeParagraph(p);
	}
	paragraphs.add(p);
	p.setChapter(this);
    }

    public boolean removeParagraph(Paragraph p) {
	return paragraphs.remove(p);
    }

    public List<Paragraph> getParagraphs() {
	return Collections.unmodifiableList(paragraphs);
    }

    public Paragraph getParagraph(int index) {
	return paragraphs.get(index);
    }

    public String getText() {
	StringBuffer sb = new StringBuffer();
	sb.append(title);
	sb.append("\n");
	for (Paragraph p : paragraphs) {
	    sb.append("\n\n");
	    sb.append(p.getText());
	}
	sb.append("\n");
	return sb.toString();
    }
}
