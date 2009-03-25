
class T85i6 {
    
	interface Super {
	    class C {}
	}
	interface I1 extends Super {}
	interface I2 extends Super {}
	class Sub implements I1, I2 {
	    C c = new Sub.C();
	}
    
}
