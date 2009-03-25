
class T15112m17 {
    
	class A {
	    int i;
	}
	class S {
	    Object i;
	}
	class B extends A {
	    Object i;
	    class Inner extends S {
		Object i;
		int j = B.super.i;
		int k = ((A) B.this).i;
	    }
	}
    
}
