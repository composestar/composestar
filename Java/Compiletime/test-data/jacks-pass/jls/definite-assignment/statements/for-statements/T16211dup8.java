
public class T16211dup8 {
    T16211dup8 (){}
    public static void main(String[] args) {
        
	boolean b = true;
	for (final int i; b && false; )
	    i = 1;
    
    }
}
