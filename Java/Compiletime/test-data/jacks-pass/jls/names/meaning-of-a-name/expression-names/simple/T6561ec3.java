
class T6561ec3 {
    
	int i;
	class Inner {
	    Inner(int i) {}
	    Inner() {
		this(i); // i is not inherited, and this$0.i is available
	    }
	}
    
}
