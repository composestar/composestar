
public class T1629dup2 {
    T1629dup2 (){}
    public static void main(String[] args) {
        
	final int i;
	while (true) {
	    try {
		i = 2;
		continue; // doesn't continue loop
	    } finally {
		return;
	    }
	}
    
    }
}
