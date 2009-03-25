
class T83i9 {
    
	interface Super {
	    int i = 1;
	}
	interface I1 extends Super {}
	interface I2 extends Super {}
	class C implements I1, I2 {
	    int j = i;
	}
    
}
