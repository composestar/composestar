
class T15213t5 {
    
	interface I {}
	final class C {}
	boolean m(I i, C c) {
	    return (i == (Object) c) || ((Object) i == c);
	}
    
}
