
class T1591qa25 {
    
	class A {}
	{
	    T1591qa25 t = this;
	    t.new A() {}; // this is legal in spite of the chapter 15 grammar
	}
    
}
