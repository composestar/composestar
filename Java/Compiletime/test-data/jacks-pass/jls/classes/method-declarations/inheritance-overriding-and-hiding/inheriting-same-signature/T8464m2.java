
class T8464m2 {
    
	interface Super {
	    void m();
	}
	interface I1 extends Super {}
	interface I2 extends Super {}
	abstract class C implements I1, I2 {
	    { m(); }
	}
    
}
