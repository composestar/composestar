
class T15123ec4 {
    
	int m() { return 1; }
	T15123ec4(int i) {}
	class Sub extends T15123ec4 {
	    Sub() {
		// calling the enclosing m, not the inherited m, is legal
		super(T15123ec4.this.m());
	    }
	}
    
}
