
class T15213t7 {
    
	interface I1 {
	    void m();
	}
	interface I2 {
	    int m();
	}
	boolean m(I1 i1, I2 i2) {
	    return ((Object) i1 == i2) || (i1 == (Object) i2);
	}
    
}
