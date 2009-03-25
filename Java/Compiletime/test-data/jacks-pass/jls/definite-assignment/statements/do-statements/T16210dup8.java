
public class T16210dup8 {
    T16210dup8 (){}
    public static void main(String[] args) {
        
	final int i;
	boolean b = true;
	do
	    i = 1;
	while (b && false);
    
    }
}
