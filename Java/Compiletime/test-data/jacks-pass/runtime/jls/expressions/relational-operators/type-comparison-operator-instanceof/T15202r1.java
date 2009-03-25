
class T15202r1 {
    static int m(char c) {
	System.out.print(c);
	return 1;
    }
    public static void main(String[] args) {
	boolean b = new int[] { m('O') } instanceof Cloneable;
	b &= "" + m('K') instanceof String;
	System.out.print(" " + b);
	b = ((m(' ') > 0) ? null : null) instanceof Object;
	System.out.print(b);
    }
}
    