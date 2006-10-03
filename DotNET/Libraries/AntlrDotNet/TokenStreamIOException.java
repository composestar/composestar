package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.cs.usfca.edu
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: TokenStreamIOException.java 1518 2006-09-20 13:13:30Z reddog33hummer $
 */

import java.io.IOException;

/**
 * Wraps an IOException in a TokenStreamException
 */
public class TokenStreamIOException extends TokenStreamException {
    public IOException io;

    /**
     * TokenStreamIOException constructor comment.
     * @param s java.lang.String
     */
    public TokenStreamIOException(IOException io) {
        super(io.getMessage());
        this.io = io;
    }
}
