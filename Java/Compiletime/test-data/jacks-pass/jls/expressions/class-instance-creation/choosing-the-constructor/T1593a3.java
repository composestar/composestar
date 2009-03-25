
class T1593a3 {
    
	static {
	    class A {}
	    Object o = new A() {}; // does not need enclosing instance
	}
    
}
