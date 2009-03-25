
class T1419e17 {
    
	void m() {
	    try {
		throw new Exception();
	    } catch (Exception e) {
	    } finally {
		return;
	    }
	}
    
}
