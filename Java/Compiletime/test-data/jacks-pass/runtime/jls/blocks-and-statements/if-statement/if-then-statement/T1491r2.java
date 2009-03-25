
class T1491r2 {
    public static void main(String [] args) {
	foo();
	System.out.print("OK");
    }
    static void foo() {
	boolean a = false;
	if (a);
	else if (false);
    }
}
