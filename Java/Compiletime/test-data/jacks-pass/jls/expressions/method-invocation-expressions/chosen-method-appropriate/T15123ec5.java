
class T15123ec5 {
    
	int m() { return 1; }
	class Inner {
	    Inner(int i) {}
	    Inner() {
		// explicit mention of the only version of m
		this(T15123ec5.this.m());
	    }
	}
    
}
