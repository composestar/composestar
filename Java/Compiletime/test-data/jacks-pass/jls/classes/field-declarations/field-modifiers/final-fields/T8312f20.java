
class T8312f20 {
    
	final int i;
	T8312f20() {
	    l: try {
		return; // discarded by abrupt finally
	    } finally {
		break l;
	    }
	    i = 1;
	}
    
}
