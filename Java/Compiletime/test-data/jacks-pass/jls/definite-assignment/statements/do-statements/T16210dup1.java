
public class T16210dup1 {
    T16210dup1 (){}
    public static void main(String[] args) {
        
	final int i;
	boolean b = true;
	do {
	    if (b)
                break;
	    try {
		i = 1;
		break; // doesn't exit do
	    } finally {
		return;
	    }
	} while (true);
	i = 2;
    
    }
}
