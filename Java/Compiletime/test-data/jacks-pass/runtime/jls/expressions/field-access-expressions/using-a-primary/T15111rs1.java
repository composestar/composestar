
class T15111rs1 {
    static T15111rs1 a() {
	System.out.print("a ");
	return t;
    }
    static int i = 0;
    static T15111rs1 t = new T15111rs1();

    T15111rs1() {}
    T15111rs1(char c) { System.out.print("c "); }
    public String toString() {
	System.out.print("d ");
	return null;
    }
    public static void main(String[] args) {
	a().i++; // method invocation
	a().t.i++; // field access
	(t.a()).i++; // parenthesized expression
	new T15111rs1('c').i++; // instance creation
	((T15111rs1)a()).i++; // safe cast
	("" + t).CASE_INSENSITIVE_ORDER.toString(); // string conversion
	(t = null).i++; // assignment
	if (t != null) System.out.print("Oops ");
	(t == null ? a() : a()).i++; // conditional
	if (i != 7) System.out.print("Oops ");
	i = 0;
	T15111rs1[] ta = {t};
	ta[i++].i++; // array access
	if (i != 2) System.out.print("Oops ");
    }
}
    