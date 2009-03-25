
class T8464m1 {
    
	interface I1 {
	    void m();
	}
	interface I2 {
	    void m();
	}
	abstract class C implements I1, I2 {
	    { m(); }
	}
    
}
