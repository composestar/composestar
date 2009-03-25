
class T15951s8 {
    
	A a = new A();
	static class A {
	    private A() {}
	    // compiler creates A(T15951s8$1 dummy) { this(); }
	    A(String s) {}
	}
	// no ambiguity between real A(String), synthetic A(dummy)
	{ new A(null) {}; }
    
}
