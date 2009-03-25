
public class T1629dup3 {
    T1629dup3 (){}
    public static void main(String[] args) {
        
	final int i;
	while (true)
	    if (false)
	        i = 1;
	// the fact that i is not DU before the loop doesn't matter
    
    }
}
