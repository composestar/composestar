
public class T1625dup1 {
    T1625dup1 (){}
    public static void main(String[] args) {
        
	final int i;
	l: {
	    if (true)
	        break l;
	    try {
		i = 1;
		break l; // cannot exit l
	    } finally {
		return;
	    }
	}
	i = 2;
    
    }
}
