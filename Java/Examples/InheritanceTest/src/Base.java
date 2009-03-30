public abstract class Base {
	public void method1() {
		System.out.println("Base.method1");
	}

	public void method2() {
		System.out.println("Base.method2");
	}

	public abstract void method3();
	
	public abstract void method4();

	public void callMethod1() {
		System.out.println("Base.callMethod1");
		method1();
	}

	public void callMethod2() {
		System.out.println("Base.callMethod2");
		method2();
	}

	public abstract void callMethod3();
	
	public void callMethod4() {
		System.out.println("Base.callMethod4");
		method4();
	}
}
