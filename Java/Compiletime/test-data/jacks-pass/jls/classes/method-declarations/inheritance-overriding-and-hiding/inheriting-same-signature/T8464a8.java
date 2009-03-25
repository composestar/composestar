
class T8464a8 {
    
	class E1 extends Exception {}
	class E2 extends Exception {}
	abstract class C {
	    public abstract void m() throws E1;
	}
	interface I {
	    void m() throws E2;
	}
	abstract class D extends C implements I {}
    
}
