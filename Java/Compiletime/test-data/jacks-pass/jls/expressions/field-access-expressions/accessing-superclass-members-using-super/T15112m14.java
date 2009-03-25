
class T15112m14 {
    
	class A {
	    private int i;
	}
	class B extends A {
	    Object i;
	    int j = B.super.i;
	    int k = ((A) B.this).i;
	}
    
}
