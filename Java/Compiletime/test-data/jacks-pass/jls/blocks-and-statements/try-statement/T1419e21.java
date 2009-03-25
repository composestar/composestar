
class T1419e21 {
    
	void m() {
	    try {
		throw new Exception();
	    } catch (Exception e) {
		throw e;
	    } finally {
		return;
	    }
	}
    
}
