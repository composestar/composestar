
public class T1629dup8 {
    T1629dup8 (){}
    public static void main(String[] args) {
        
	final boolean b;
	b = true;
	while (b || true);
	b = false;
    
    }
}
