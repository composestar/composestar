
class T15112m7 {
    
	class A {
	    private int i;
	}
	class B extends A {
	    Object i;
	    int j = super.i;
	    int k = ((A) this).i;
	}
    
}
