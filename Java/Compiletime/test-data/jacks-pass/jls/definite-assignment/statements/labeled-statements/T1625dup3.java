
public class T1625dup3 {
    T1625dup3 (){}
    public static void main(String[] args) {
        
	final int i;
	boolean b = true;
	l: {
	    if (b)
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
