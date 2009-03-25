
public class T16211dup2 {
    T16211dup2 (){}
    public static void main(String[] args) {
        
	final int i;
	for ( ; ; ) {
	    try {
		i = 2;
		continue; // doesn't continue loop
	    } finally {
		return;
	    }
	}
    
    }
}
