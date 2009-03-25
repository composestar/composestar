
class T1419e13 {
    
	void m() {
	    try {
		throw new Exception();
	    } finally {
		return;
	    }
	}
    
}
