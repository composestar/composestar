package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.cs.usfca.edu
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: TokenStream.java,v 1.1 2006/02/13 09:12:47 pascal Exp $
 */

public interface TokenStream {
    public Token nextToken() throws TokenStreamException;
}
