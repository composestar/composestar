
class T8851e4 {
    
	static class Top {
	    int x;
	    class Dummy {
		Dummy(Object o) {}
	    }
	    class Inside extends Dummy {
		Inside(final int y) {
		    super(new Object() { int r = y; }); // correct
		}
	    }
	}
    
}
