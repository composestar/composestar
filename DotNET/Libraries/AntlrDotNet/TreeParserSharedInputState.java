package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.cs.usfca.edu
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: TreeParserSharedInputState.java 1518 2006-09-20 13:13:30Z reddog33hummer $
 */

/** This object contains the data associated with an
 *  input AST.  Multiple parsers
 *  share a single TreeParserSharedInputState to parse
 *  the same tree or to have the parser walk multiple
 *  trees.
 */
public class TreeParserSharedInputState {
    /** Are we guessing (guessing>0)? */
    public int guessing = 0;
}
