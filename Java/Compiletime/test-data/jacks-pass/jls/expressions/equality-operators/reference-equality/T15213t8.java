
class T15213t8 {
    
	interface I {
	    void m();
	}
	class C {
	    int m() { return 1; }
	}
	boolean m(I i, C c) {
	    // Note that no subclass of C can implement i!
	    return i == c;
	}
    
}
