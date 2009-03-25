
import p1.T15112r1a;
class T15112r1b extends T15112r1a {
    T15112r1b(String s) { super(s); }
    String a = "4";
    String b = "5";
    class Inner extends T15112r1a {
	String a = "6";
	String b = "7";
	Inner(String s) { super(s); }
	void m() {
	    System.out.print(T15112r1b.super.a + " " + T15112r1b.super.b);
	    System.out.print(" " + Inner.super.b + " " + super.b);
	}
    }
    public static void main(String[] args) {
	new T15112r1b("2").new Inner("3").m();
    }
}
    