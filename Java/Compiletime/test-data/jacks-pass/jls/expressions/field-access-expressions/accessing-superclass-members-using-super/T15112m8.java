
class T15112m8 {
    
	class A {
	    int i;
	}
	class B extends A {
	    Object i;
	    int j = B.super.i;
	    int k = ((A) B.this).i;
	}
    
}
