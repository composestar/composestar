package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.cs.usfca.edu
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: ASTVisitor.java 1518 2006-09-20 13:13:30Z reddog33hummer $
 */

import antlr.collections.AST;

public interface ASTVisitor {
    public void visit(AST node);
}
