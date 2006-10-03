package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.cs.usfca.edu
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: OneOrMoreBlock.java 1518 2006-09-20 13:13:30Z reddog33hummer $
 */

class OneOrMoreBlock extends BlockWithImpliedExitPath {

    public OneOrMoreBlock(Grammar g) {
        super(g);
    }

    public OneOrMoreBlock(Grammar g, Token start) {
        super(g, start);
    }

    public void generate() {
        grammar.generator.gen(this);
    }

    public Lookahead look(int k) {
        return grammar.theLLkAnalyzer.look(k, this);
    }

    public String toString() {
        return super.toString() + "+";
    }
}
