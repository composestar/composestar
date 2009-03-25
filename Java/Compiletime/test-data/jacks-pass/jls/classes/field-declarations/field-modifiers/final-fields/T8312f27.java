
class T8312f27 {
    
	final int i;
	T8312f27(boolean b) {
	    l: try {
		if (b)
		    return; // intervening finally assigns i
		else
		    break l; // intervening finally assigns i
	    } finally {
		i = 1;
	    }
	}
    
}
