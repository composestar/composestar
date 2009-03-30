public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// The following code should print the same results twice.
		// all calls should be preceded with a [incoming] line
		// and all callMethodN calls should be followed up with an [outgoing]
		// line, following with another [incoming] line

		Base b = new Child();
		b.method1();
		b.method2();
		b.method3();
		b.method4();
		b.callMethod1();
		b.callMethod2();
		b.callMethod3();
		b.callMethod4();

		System.out.println("---");

		Child c = new Child();
		c.method1();
		c.method2();
		c.method3();
		c.method4();
		c.callMethod1();
		c.callMethod2();
		c.callMethod3();
		c.callMethod4();
	}
}
