
class T1419e20 {
    
	void m() {
	    try {
		try {
		    throw new Exception();
		} catch (Exception e) {
		} finally {
		    throw new RuntimeException();
		}
	    } catch (RuntimeException e) {
	    }
	}
    
}
