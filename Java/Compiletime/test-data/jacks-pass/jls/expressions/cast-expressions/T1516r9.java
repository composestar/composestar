
class T1516r9 {
    
	interface I { int m(); }
	interface J { void m(); }
	void m(J j) {
	    I i = (I) j;
	}
    
}
