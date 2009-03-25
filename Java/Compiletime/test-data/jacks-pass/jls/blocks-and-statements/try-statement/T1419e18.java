
class T1419e18 {
    
	void m() {
	    a:
	    try {
		throw new Exception();
	    } catch (Exception e) {
	    } finally {
		break a;
	    }
	}
    
}
