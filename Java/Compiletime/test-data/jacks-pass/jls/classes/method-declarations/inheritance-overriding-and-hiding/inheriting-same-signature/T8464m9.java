
class T8464m9 {
    
	interface I {
	    void m();
	}
	class C {
	    public void m() {}
	}
	class D extends C implements I {
	    { m(); }
	}
    
}
