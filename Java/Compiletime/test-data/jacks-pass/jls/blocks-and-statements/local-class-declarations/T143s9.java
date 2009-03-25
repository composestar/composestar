
class T143s9 {
    
	class Local {
	    static final int i = 1;
	}
	void m(int j) {
	    switch (j) {
		case 0:
		case (1 == Local.i ? 1 : 0):
	    }
	    class Local {
		static final int i = 2;
	    }
	    switch (j) {
		case 0:
		case (2 == Local.i ? 1 : 0):
	    }
	}
    
}
