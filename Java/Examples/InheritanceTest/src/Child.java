public class Child extends Base {
	@Override
	public void method2() {
		System.out.println("Child.method2");
	}

	@Override
	public void method3() {
		System.out.println("Child.method3");
	}
	
	@Override
	public void method4() {
		System.out.println("Child.method4");
	}

	@Override
	public void callMethod2() {
		System.out.println("Child.callMethod2");
		method2();
	}

	@Override
	public void callMethod3() {
		System.out.println("Child.callMethod3");
		method3();
	}
}
