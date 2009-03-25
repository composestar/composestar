
class T8464a7 {
    
	abstract class C {
	    abstract void m(); // note non-public accessibility
	}
	interface I {
	    void m();
	}
	abstract class D extends C implements I {}
    
}
