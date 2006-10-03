package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.cs.usfca.edu
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: StringLiteralSymbol.java 1518 2006-09-20 13:13:30Z reddog33hummer $
 */

class StringLiteralSymbol extends TokenSymbol {
    protected String label;	// was this string literal labeled?


    public StringLiteralSymbol(String r) {
        super(r);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
