
class T15202s7 {
    
	interface I extends Cloneable {}
	I i = new I() {};
	// widening to superinterface
	boolean b = i instanceof Cloneable;
    
}
