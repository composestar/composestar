package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.cs.usfca.edu
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: ANTLRException.java,v 1.1 2006/02/13 09:12:46 pascal Exp $
 */

public class ANTLRException extends Exception {

    public ANTLRException() {
        super();
    }

    public ANTLRException(String s) {
        super(s);
    }

	public ANTLRException(String message, Throwable cause) {

		super(cause == null ? message : (message + " CAUSE:" + cause.toString()));
	}
	
	public ANTLRException(Throwable cause) {
		this("",cause);
	}
}
