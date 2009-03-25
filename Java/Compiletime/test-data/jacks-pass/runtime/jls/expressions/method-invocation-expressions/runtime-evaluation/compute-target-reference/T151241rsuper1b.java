
import p1.T151241rsuper1a;
class T151241rsuper1b extends T151241rsuper1a {
    public String a() { return "1"; }
    protected String i() { return "3"; }
    protected static String s() { return "4"; }
    class Inner extends T151241rsuper1a {
	public String a() { return "5"; }
	protected String i() { return "6"; }
	void m() {
	    System.out.print(T151241rsuper1b.super.i() + " ");
	    System.out.print(T151241rsuper1b.super.s() + " ");
	    System.out.print(Inner.super.i() + " " + super.i());
	}
    }
    public static void main(String[] args) {
	new T151241rsuper1b().new Inner().m();
    }
}
    