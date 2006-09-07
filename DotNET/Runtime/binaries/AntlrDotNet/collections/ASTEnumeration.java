package antlr.collections;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.cs.usfca.edu
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: ASTEnumeration.java,v 1.1 2006/02/13 09:12:48 pascal Exp $
 */

public interface ASTEnumeration {
    public boolean hasMoreNodes();

    public AST nextNode();
}
