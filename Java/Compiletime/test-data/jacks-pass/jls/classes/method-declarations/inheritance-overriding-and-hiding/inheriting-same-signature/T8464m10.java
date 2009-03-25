
class T8464m10 {
    
	interface I {
	    void m();
	}
	abstract class C implements I {}
	abstract class D extends C implements I {
	    { m(); }
	}
    
}
