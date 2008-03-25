package bookshelf.generators;

import java.util.Random;

import bookshelf.books.Book;
import bookshelf.books.Chapter;
import bookshelf.books.Paragraph;

public class ArmyOfMonkeys {
    public static int BOOK_TITLE_MIN = 2; // words
    public static int BOOK_TITLE_MAX = 10;
    public static int BOOK_AUTHOR_MIN = 2; // words
    public static int BOOK_AUTHOR_MAX = 3;
    public static int BOOK_CHAPTER_MIN = 2; // chapters
    public static int BOOK_CHAPTER_MAX = 10;
    public static int CHAPTER_TITLE_MIN = 3; // words
    public static int CHAPTER_TITLE_MAX = 8;
    public static int CHAPTER_PARAGRAPH_MIN = 5; // paragraphs
    public static int CHAPTER_PARAGRAPH_MAX = 15;
    public static int PARAGRAPH_MIN = 50; // words
    public static int PARAGRAPH_MAX = 500;
    public static int LINE_MIN = 5; // words
    public static int LINE_MAX = 15;

    protected static final Random random = new Random();

    public static Book writeBook() {
	return writeBook(getWords(BOOK_TITLE_MIN
		+ random.nextInt(BOOK_TITLE_MAX - BOOK_TITLE_MIN)));
    }

    public static Book writeBook(String title) {
	return writeBook(title, getWords(BOOK_AUTHOR_MIN
		+ random.nextInt(BOOK_AUTHOR_MAX - BOOK_AUTHOR_MIN)));
    }

    public static Book writeBook(String title, String author) {
	Book book = new Book(title, author);
	int cnt = BOOK_CHAPTER_MIN
		+ random.nextInt(BOOK_CHAPTER_MAX - BOOK_CHAPTER_MIN);
	for (int i = 0; i < cnt; i++) {
	    book.addChapters(writeChapter());
	}
	return book;
    }

    public static Chapter writeChapter() {
	return writeChapter(getWords(CHAPTER_TITLE_MIN
		+ random.nextInt(CHAPTER_TITLE_MAX - CHAPTER_TITLE_MIN)));
    }

    public static Chapter writeChapter(String title) {
	Chapter chapter = new Chapter(title);
	for (int i = 0; i < CHAPTER_PARAGRAPH_MIN
		+ random.nextInt(CHAPTER_PARAGRAPH_MAX - CHAPTER_PARAGRAPH_MIN); i++) {
	    chapter.addParagraph(writeParagraph());
	}
	return chapter;
    }

    public static Paragraph writeParagraph() {
	return new Paragraph(getLines(PARAGRAPH_MIN
		+ random.nextInt(PARAGRAPH_MAX - PARAGRAPH_MIN)));
    }

    protected static final String wordSource = "lorem ipsum dolor sit amet consectetur adipisicing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua ut enim ad minim veniam quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur excepteur sint occaecat cupidatat non proident sunt in culpa qui officia deserunt mollit anim id est laborum";
    protected static String[] words = null;

    public static String getWords(int wordCount) {
	if (words == null) {
	    words = wordSource.split("\\s");
	}
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < wordCount; i++) {
	    if (i > 0) {
		sb.append(" ");
	    }
	    sb.append(words[random.nextInt(words.length)]);
	}
	return sb.toString();
    }

    public static String getLines(int wordCount) {
	if (words == null) {
	    words = wordSource.split("\\s");
	}
	StringBuffer sb = new StringBuffer();
	int nextLine = 0;
	for (int i = 0; i < wordCount; i++) {
	    if (nextLine <= 0) {
		nextLine = LINE_MIN + random.nextInt(LINE_MAX - LINE_MIN);
		if (i > 0) {
		    sb.append(". ");
		}
		String word = words[random.nextInt(words.length)];
		sb.append(word.toUpperCase().charAt(0));
		sb.append(word.substring(1));
	    } else {
		sb.append(" ");
		sb.append(words[random.nextInt(words.length)]);
	    }
	    nextLine--;
	}
	sb.append(".");
	return sb.toString();
    }
}
