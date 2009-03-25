
class T151241rs2 {
    static int i = 0;
    static T151241rs2 a() { throw new RuntimeException(); }
    static void b(int i) { System.out.print("Oops "); }
    static T151241rs2 t = null;
    T151241rs2 t1 = null;

    T151241rs2() {}
    T151241rs2(int i) { throw new IllegalArgumentException(); }
    public String toString() {
	int i = 0;
	i /= i; // throw ArithmeticException, rather than return
	return null;
    }
    public static void main(String[] args) {
	try {
	    t.t1.b(i++); // instance field access
	} catch (NullPointerException e) {
	    System.out.print("1 ");
	}
	T151241rs2[] ta = {};
	try {
	    ta[0].b(i++); // array access
	} catch (ArrayIndexOutOfBoundsException e) {
	    System.out.print("2 ");
	}
	try {
	    a().b(i++); // method call
	} catch (RuntimeException e) {
	    System.out.print("3 ");
	}
	Object o = "";
	try {
	    ((T151241rs2)o).b(i++); // unsafe cast
	} catch (ClassCastException e) {
	    System.out.print("4 ");
	}
	try {
	    ("" + new T151241rs2()).valueOf(1); // abrupt binary expression
	    System.out.print("Oops ");
	} catch (ArithmeticException e) {
	    System.out.print("5 ");
	}
	try {
	    new T151241rs2(1).b(i++); // constructor
	} catch (IllegalArgumentException e) {
	    System.out.print("6");
	}
	if (i != 0) System.out.print("Oops ");
    }
}
    