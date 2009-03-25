
class T1419e2 {
    
	void m() throws Exception {
	    try {
		throw new Exception();
	    } catch (RuntimeException e) {
	    }
	}
    
}
