
class T1419e10 {
    
	void m() throws Exception {
	    try {
		throw new Exception();
	    } catch (Exception e) {
		throw e;
	    } finally {
	    }
	}
    
}
