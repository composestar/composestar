
class T15123ec3 {
    
	int m() { return 1; }
	class Inner {
	    Inner(int i) {}
	    Inner() {
		this(m()); // m is not inherited, and this$0.m is available
	    }
	}
    
}
