
class T15121t14 {
    
	class A {
	    void m() {}
	}
	class B extends A {
	    { B.super.m(); }
	}
    
}
