class T1593c8a {
    A a = new A();
    static class A {
	private A() {}
	// compiler creates A(T1593c8a$1 dummy) { this(); }
	A(String s) {}
    }
}

class T1593c8b {
    // no ambiguity between real A(String), synthetic A(dummy)
    { new T1593c8a.A(null); }
}
    
