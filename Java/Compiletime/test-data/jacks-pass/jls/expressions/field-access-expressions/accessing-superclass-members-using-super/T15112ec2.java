
class T15112ec2 {
    
	class One {
	    int i;
	}
	class Two extends One {
	    class Inner {
		Inner(int i) {}
		Inner() {
		    // calling super field of enclosing class is legal
		    this(Two.super.i);
		}
	    }
	}
    
}
