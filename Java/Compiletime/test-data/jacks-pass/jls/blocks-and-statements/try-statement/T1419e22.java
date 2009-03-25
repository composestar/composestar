
class T1419e22 {
    
	void m() {
	    a:
	    try {
		throw new Exception();
	    } catch (Exception e) {
		throw e;
	    } finally {
		break a;
	    }
	}
    
}
