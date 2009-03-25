
public class T1629dup1 {
    T1629dup1 (){}
    public static void main(String[] args) {
        
	final int i;
	boolean b = true;
	while (true) {
	    if (b)
	        break;
	    try {
		i = 1;
		break; // doesn't exit while
	    } finally {
		return;
	    }
	}
	i = 2;
    
    }
}
