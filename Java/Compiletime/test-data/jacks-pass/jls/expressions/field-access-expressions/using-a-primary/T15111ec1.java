
class T15111ec1 {
    
	int i;
	T15111ec1(int i) {}
	class Sub extends T15111ec1 {
	    Sub() {
		// using the enclosing i, not the inherited i, is legal
		super(T15111ec1.this.i);
	    }
	}
    
}
