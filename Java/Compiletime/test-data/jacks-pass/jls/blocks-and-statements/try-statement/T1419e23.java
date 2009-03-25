
class T1419e23 {
    
	void m() {
	    a: do
	    try {
		throw new Exception();
	    } catch (Exception e) {
		throw e;
	    } finally {
		continue a;
	    }
	    while (false);
	}
    
}
