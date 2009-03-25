
class T151222a14 {
    
	class E1 extends Exception {}
	class E2 extends Exception {}
	abstract class C1 {
	    abstract void m() throws E1; // note non-public accessibility
	}
	interface I {
	    void m() throws E2;
	}
	abstract class C2 extends C1 implements I {
	    { m(); }
	}
    
}
