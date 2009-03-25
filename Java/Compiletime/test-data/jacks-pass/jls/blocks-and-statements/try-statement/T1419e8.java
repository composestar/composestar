
class T1419e8 {
    
	void m() throws Exception {
	    try {
		throw new Exception();
	    } catch (Exception e) {
		throw e;
	    }
	}
    
}
