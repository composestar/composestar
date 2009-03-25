
import p1.T15112r2a;
class T15112r2b extends T15112r2a {
    T15112r2b(int i) { super(i); }
    int a = 8;
    int b = 9;
    class Inner extends T15112r2a {
	int a = 10;
	int b = 11;
	Inner(int i) { super(i); }
	void m() {
	    System.out.print(++T15112r2b.super.a + " " + ++T15112r2b.super.b);
	    System.out.print(" " + ++Inner.super.b + " " + ++super.b);
	}
    }
    public static void main(String[] args) {
	new T15112r2b(3).new Inner(5).m();
    }
}
    