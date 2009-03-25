
class T8312f26 {
    
	final int i;
	T8312f26(boolean b) {
	    try {
		if (b)
		    return; // intervening finally assigns i
	    } finally {
		i = 1;
	    }
	}
    
}
