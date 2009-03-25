
class T1419e14 {
    
	void m() {
	    a:
	    try {
		throw new Exception();
	    } finally {
		break a;
	    }
	}
    
}
