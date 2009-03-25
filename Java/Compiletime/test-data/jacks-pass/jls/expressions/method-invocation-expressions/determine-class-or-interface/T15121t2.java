
class T15121t2 {
    
	void m() {}
	class A {
	    void m(int i) {}
	    { m(1); } // A is searched, not T15121t1; and A.m(int) exists
	}
    
}
