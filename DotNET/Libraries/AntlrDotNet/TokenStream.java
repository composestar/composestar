package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.cs.usfca.edu
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: TokenStream.java 1518 2006-09-20 13:13:30Z reddog33hummer $
 */

public interface TokenStream {
    public Token nextToken() throws TokenStreamException;
}
