
class T1593c7 {
    
	A a = new A();
	static class A {
	    private A() {}
	    // compiler creates A(T1593c7$1 dummy) { this(); }
	    A(String s) {}
	}
	// no ambiguity between real A(String), synthetic A(dummy)
	{ new A(null); }
    
}
