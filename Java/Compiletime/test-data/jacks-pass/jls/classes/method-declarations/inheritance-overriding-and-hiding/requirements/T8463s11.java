
class T8463s11 {
    
	class E1 extends Exception {}
	class E2 extends Exception {}
	abstract class C1 {
	    public abstract void m() throws E1;
	}
	interface I {
	    void m() throws E2;
	}
	abstract class C2 extends C1 implements I {}
	class D extends C2 {
	    public void m() {}
	}
    
}
