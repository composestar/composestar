package bookshelf.books;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Book {
    protected String title;
    protected String author;
    protected List<Chapter> chapters;

    public Book(String bookTitle, String bookAuthor) {
	title = bookTitle;
	author = bookAuthor;
	chapters = new ArrayList<Chapter>();
    }

    public Book(String bookTitle) {
	this(bookTitle, "Anonymous");
    }

    public Book() {
	this("Untitled");
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String newTitle) {
	title = newTitle;
    }

    public String getAuthor() {
	return author;
    }

    public void setAuthor(String newAuthor) {
	author = newAuthor;
    }

    public List<Chapter> getChapters() {
	return Collections.unmodifiableList(chapters);
    }

    public Chapter getChapter(int index) {
	return chapters.get(index);
    }

    public void addChapter(Chapter newChapter) {
	Book pb = newChapter.withinBook();
	if (pb != null && pb != this) {
	    pb.removeChapter(newChapter);
	}
	chapters.add(newChapter);
	newChapter.setBook(this);
    }

    public boolean removeChapter(Chapter chapter) {
	return chapters.remove(chapter);
    }

    public String getText() {
	StringBuffer sb = new StringBuffer();
	sb.append("\n==================================================\n");
	sb.append(title);
	sb.append("\nby ");
	sb.append(author);
	sb.append("\n==================================================\n");
	int cnt = 1;
	for (Chapter c : chapters) {
	    sb.append("\n\nChapter ");
	    sb.append(cnt++);
	    sb.append(": ");
	    sb.append(c.getText());
	}
	sb.append("\n");
	return sb.toString();
    }
}
