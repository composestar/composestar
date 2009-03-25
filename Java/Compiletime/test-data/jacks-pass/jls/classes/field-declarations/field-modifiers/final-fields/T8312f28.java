
class T8312f28 {
    
	final int i;
	T8312f28(boolean b) {
	    l: try {
		break l; // intervening finally assigns i
	    } finally {
		i = 1;
	    }
	}
    
}
