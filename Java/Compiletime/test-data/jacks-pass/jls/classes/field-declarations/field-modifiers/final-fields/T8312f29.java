
class T8312f29 {
    
	final int i;
	T8312f29(boolean b) {
	    l: do
	        try {
		    continue l; // intervening finally assigns i
		} finally {
		    i = 1;
		}
	    while (false);
	}
    
}
