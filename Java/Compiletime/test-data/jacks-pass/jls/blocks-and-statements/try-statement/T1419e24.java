
class T1419e24 {
    
	void m() {
	    try {
		try {
		    throw new Exception();
		} catch (Exception e) {
		    throw e;
		} finally {
		    throw new RuntimeException();
		}
	    } catch (RuntimeException e) {
	    }
	}
    
}
