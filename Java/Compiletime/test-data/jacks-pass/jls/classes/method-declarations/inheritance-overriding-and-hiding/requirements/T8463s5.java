
class T8463s5 {
    
	class E1 extends Exception {}
	class E2 extends Exception {}
	class E3 extends Exception {}
	interface I1 {
	    void m() throws E1, E2;
	}
	interface I2 {
	    void m() throws E2, E3;
	}
	class C implements I1, I2 {
	    public void m() throws E2 {}
	}
	class D extends C {
	    public void m() {}
	}
    
}
