package bookshelf.books;

public class Paragraph {
    protected Chapter chapter;
    protected String text;

    public Paragraph() {
	text = "";
    }

    public Paragraph(String withText) {
	text = withText;
    }

    public void setChapter(Chapter inChapter) {
	chapter = inChapter;
    }

    public Chapter withinChapter() {
	return chapter;
    }

    public void addText(String moreText) {
	text += moreText;
    }

    public void setText(String newText) {
	text = newText;
    }

    public String getText() {
	return text;
    }
}
