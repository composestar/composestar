
public class T1625dup2 {
    T1625dup2 (){}
    public static void main(String[] args) {
        
	final int i;
	l: {
	    if (false)
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
