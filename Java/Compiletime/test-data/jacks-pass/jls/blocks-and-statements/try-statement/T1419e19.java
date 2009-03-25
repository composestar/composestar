
class T1419e19 {
    
	void m() {
	    a: do
	    try {
		throw new Exception();
	    } catch (Exception e) {
	    } finally {
		continue a;
	    }
	    while (false);
	}
    
}
