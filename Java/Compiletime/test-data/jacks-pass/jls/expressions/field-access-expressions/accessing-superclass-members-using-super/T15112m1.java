
class T15112m1 {
    
	class A {
	    int i;
	}
	class B extends A {
	    Object i;
	    int j = super.i;
	    int k = ((A) this).i;
	}
    
}
