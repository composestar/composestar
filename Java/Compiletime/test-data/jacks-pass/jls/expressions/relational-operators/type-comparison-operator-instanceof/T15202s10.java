
class T15202s10 {
    
	interface I {}
	Cloneable c = new Cloneable() {};
	// narrowing to compatible interface
	boolean b = c instanceof I;
    
}
