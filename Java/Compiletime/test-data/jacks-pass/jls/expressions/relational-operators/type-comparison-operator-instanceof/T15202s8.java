
class T15202s8 {
    
	interface I extends Cloneable {}
	Cloneable c = new Cloneable() {};
	// narrowing to subinterface
	boolean b = c instanceof I;
    
}
