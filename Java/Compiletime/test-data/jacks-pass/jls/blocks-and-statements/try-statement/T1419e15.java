
class T1419e15 {
    
	void m() {
	    a: do
	    try {
		throw new Exception();
	    } finally {
		continue a;
	    }
	    while (false);
	}
    
}
