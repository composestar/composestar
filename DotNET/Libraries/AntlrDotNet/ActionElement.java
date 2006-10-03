package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.cs.usfca.edu
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: ActionElement.java 1518 2006-09-20 13:13:30Z reddog33hummer $
 */

class ActionElement extends AlternativeElement {
    protected String actionText;
    protected boolean isSemPred = false;


    public ActionElement(Grammar g, Token t) {
        super(g);
        actionText = t.getText();
        line = t.getLine();
        column = t.getColumn();
    }

    public void generate() {
        grammar.generator.gen(this);
    }

    public Lookahead look(int k) {
        return grammar.theLLkAnalyzer.look(k, this);
    }

    public String toString() {
        return " " + actionText + (isSemPred?"?":"");
    }
}
