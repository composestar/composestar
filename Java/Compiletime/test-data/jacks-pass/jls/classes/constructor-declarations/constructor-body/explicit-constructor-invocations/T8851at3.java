
class T8851at3 {
    
	A a = new A();
	static class A {
	    private A() {}
	    // compiler creates A(T8851at3$1 dummy) { this(); }
	    A(String s) {}
	    A(int i) {
		// no ambiguity between real A(String), synthetic A(dummy)
		this(null);
	    }
	}
    
}
