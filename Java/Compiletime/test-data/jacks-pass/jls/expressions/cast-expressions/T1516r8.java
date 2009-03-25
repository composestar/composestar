
class T1516r8 {
    
	interface I {}
	interface J {}
	void m(J j) {
	    I i = (I) j;
	}
    
}
