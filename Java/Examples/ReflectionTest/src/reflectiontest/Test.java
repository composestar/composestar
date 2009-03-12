package reflectiontest;

/**
 * Test case for Compose* / Java to test the message and join point reflection.
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// From a static sender
		System.out.println(">> Test #0");
		A a = new A();
		a.foo();
		a.bar("test0");
		a.quux(1, "test 0");

		// From a instance sender
		System.out.println(">> Test #1");
		Test t = new Test();
		t.test1();

		System.out.println(">> Test #2");
		test2();

		System.out.println(">> Test #3");
		Inspector inspector = new Inspector();
		inspector.report();
	}

	protected void test1() {
		A a = new A();
		a.foo();
		a.bar("test1");
		a.quux(1, "test 1");
	}

	protected static void test2() {
		Runnable r = new Runnable() {
			public void run() {
				A a = new A();
				a.foo();
				a.bar("test2");
				a.quux(1, "test 2");
			}
		};
		Thread t = new Thread(r);
		t.start();
		while (t.isAlive()) {
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
			}
		}
	}
}
