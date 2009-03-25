
class T8851as4 {
    
	A a = new A();
	static class A {
	    private A() {}
	    // compiler creates A(T8851as4$1 dummy) { this(); }
	    A(String s) {}
	}
	static class B extends A {
	    B() {
		// no ambiguity between real A(String), synthetic A(dummy)
		super(null);
	    }
	}
    
}
