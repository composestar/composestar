
class T6551n17 {
    
	static class A {
	    interface B {}
	}
	final static class B extends A {
	    { new B() {}; } // the inherited T6551n16.A.B shadows T6551n16.B

	}
    
}
