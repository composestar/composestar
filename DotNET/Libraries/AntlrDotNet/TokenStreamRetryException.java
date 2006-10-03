package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.cs.usfca.edu
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: TokenStreamRetryException.java 1518 2006-09-20 13:13:30Z reddog33hummer $
 */

/**
 * Aborted recognition of current token. Try to get one again.
 * Used by TokenStreamSelector.retry() to force nextToken()
 * of stream to re-enter and retry.
 */
public class TokenStreamRetryException extends TokenStreamException {
    public TokenStreamRetryException() {
    }
}
