
class T15123ec7 {
    
	class One {
	    int m() { return 1; }
	}
	class Two extends One {
	    class Inner {
		Inner(int i) {}
		Inner() {
		    // calling super method of enclosing class is legal
		    this(Two.super.m());
		}
	    }
	}
    
}
