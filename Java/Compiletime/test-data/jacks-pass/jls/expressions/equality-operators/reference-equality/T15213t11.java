
class T15213t11 {
    
	interface I {}
	boolean m(I i, int[] a) {
	    return ((Object)i == a) || (i == (Object) a);
	}
    
}
