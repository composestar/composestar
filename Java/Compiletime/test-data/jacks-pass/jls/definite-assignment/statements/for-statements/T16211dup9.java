
public class T16211dup9 {
    T16211dup9 (){}
    public static void main(String[] args) {
        
	final boolean b;
	for (b = true; b || true; );
	b = false;
    
    }
}
