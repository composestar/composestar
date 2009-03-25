
public class T16211dup3 {
    T16211dup3 (){}
    public static void main(String[] args) {
        
	final int i;
	for ( ; ; )
	    if (false)
	        i = 1; // assignment not reachable
	// the fact that i is not DU before the loop doesn't matter
    
    }
}
