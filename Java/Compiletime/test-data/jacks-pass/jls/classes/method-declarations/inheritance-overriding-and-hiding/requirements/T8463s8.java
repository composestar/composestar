
class T8463s8 {
    
	class E1 extends Exception {}
	class E2 extends Exception {}
	interface I1 {
	    void m() throws E1;
	}
	interface I2 {
	    void m() throws E2;
	}
	abstract class C implements I1, I2 {}
	class D extends C {
	    public void m() {}
	}
    
}
