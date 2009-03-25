
class T15111ec2 {
    
	int i;
	class Inner {
	    Inner(int i) {}
	    Inner() {
		// explicit mention of the only version of i
		this(T15111ec2.this.i);
	    }
	}
    
}
