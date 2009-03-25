
class T8312f25 {
    
	final int i;
	T8312f25() {
	    try {
		return; // intervening finally assigns i
	    } finally {
		i = 1;
	    }
	}
    
}
