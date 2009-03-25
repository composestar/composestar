
class T1591qc23 {
    
	class A {}
	{
	    T1591qc23 t = this;
	    t.new A(); // this is legal in spite of the chapter 15 grammar
	}
    
}
