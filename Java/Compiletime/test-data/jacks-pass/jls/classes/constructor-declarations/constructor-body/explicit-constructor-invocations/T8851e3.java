
class T8851e3 {
    
	static class Top {
	    int x;
	    class Dummy {
		Dummy(Object o) {}
	    }
	    class Inside extends Dummy {
		Inside() {
		    super(new Object() { int r = x; }); // error
		}
	    }
	}
    
}
