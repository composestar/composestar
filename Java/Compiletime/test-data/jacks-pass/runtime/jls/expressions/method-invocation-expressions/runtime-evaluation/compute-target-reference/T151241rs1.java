
class T151241rs1 {
    static T151241rs1 a() {
	System.out.print("a ");
	return t;
    }
    static void b() { System.out.print("b "); }
    static T151241rs1 t = new T151241rs1();

    T151241rs1() {}
    T151241rs1(char c) { System.out.print("c "); }
    public String toString() {
	System.out.print("d ");
	return null;
    }
    public static void main(String[] args) {
	a().b(); // method invocation
	a().t.b(); // field access
	(t.a()).b(); // parenthesized expression
	new T151241rs1('c').b(); // instance creation
	((T151241rs1)a()).b(); // safe cast
	("" + t).valueOf(1); // string conversion
	(t = null).b(); // assignment
	if (t != null) System.out.print("Oops ");
	(t == null ? a() : a()).b(); // conditional
	T151241rs1[] ta = {t};
	int i = 0;
	ta[i++].b(); // array access
	if (i != 1) System.out.print("Oops ");
    }
}
    