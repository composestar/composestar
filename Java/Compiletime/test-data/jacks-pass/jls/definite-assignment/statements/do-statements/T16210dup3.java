
public class T16210dup3 {
    T16210dup3 (){}
    public static void main(String[] args) {
        
	final int i;
	do
	    if (false)
	        i = 1; // assignment not reachable
	while (true);
	// the fact that i is not DU before the loop doesn't matter
    
    }
}
