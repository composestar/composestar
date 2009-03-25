
class T6561ec4 {
    
	private int i;
	T6561ec4(int i) {}
	class Sub extends T6561ec4 {
	    Sub() {
		super(i); // i is not inherited, so it is the enclosing i
	    }
	}
    
}
