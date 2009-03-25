
class T15123ec9 {
    
	private int m() { return 1; }
	T15123ec9(int i) {}
	class Sub extends T15123ec9 {
	    Sub() {
		super(m()); // m is not inherited, so it is the enclosing m
	    }
	}
    
}
