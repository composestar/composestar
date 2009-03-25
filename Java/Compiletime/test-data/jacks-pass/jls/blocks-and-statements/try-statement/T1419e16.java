
class T1419e16 {
    
	void m() {
	    try {
		try {
		    throw new Exception();
		} finally {
		    throw new RuntimeException();
		}
	    } catch (RuntimeException e) {
	    }
	}
    
}
