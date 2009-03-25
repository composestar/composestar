
class T15112u11 {
    
	class A {
	    int i;
	}
	interface B {
	    Object i = null;
	}
	class C extends A implements B {
	    int j = super.i; // refers to A.i, not B.i
	}
    
}
