
public class T16210dup2 {
    T16210dup2 (){}
    public static void main(String[] args) {
        
	final int i;
	do {
	    try {
		i = 2;
		continue; // doesn't continue loop
	    } finally {
		return;
	    }
	} while (true);
    
    }
}
