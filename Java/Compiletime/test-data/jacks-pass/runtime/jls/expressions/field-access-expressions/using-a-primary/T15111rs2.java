
class T15111rs2 {
    static int i = 0;
    static T15111rs2 a() { throw new RuntimeException(); }
    static T15111rs2 t = null;
    T15111rs2 t1 = null;

    T15111rs2() {}
    T15111rs2(int i) { throw new IllegalArgumentException(); }
    public String toString() {
	int i = 0;
	i /= i; // throw ArithmeticException, rather than return
	return null;
    }
    public static void main(String[] args) {
	try {
	    t.t1.i++; // instance field access
	} catch (NullPointerException e) {
	    System.out.print("1 ");
	}
	T15111rs2[] ta = {};
	try {
	    ta[0].i++; // array access
	} catch (ArrayIndexOutOfBoundsException e) {
	    System.out.print("2 ");
	}
	try {
	    a().i++; // method call
	} catch (RuntimeException e) {
	    System.out.print("3 ");
	}
	Object o = "";
	try {
	    ((T15111rs2)o).i++; // unsafe cast
	} catch (ClassCastException e) {
	    System.out.print("4 ");
	}
	try {
	    new T15111rs2(1).i++; // constructor
	} catch (IllegalArgumentException e) {
	    System.out.print("5");
	}
	if (i != 0) System.out.print("Oops ");
    }
}
    