
public class T16211dup1 {
    T16211dup1 (){}
    public static void main(String[] args) {
        
	final int i;
	boolean b = true;
	for ( ; ; ) {
	    if (b)
	        break;
	    try {
		i = 1;
		break; // doesn't exit for
	    } finally {
		return;
	    }
	}
	i = 2;
    
    }
}
