
public class T1524o4 {
    T1524o4 (){}
    public static void main(String[] args) {
        
	boolean b = true;
	if (false || b);
	if (false || !b);
	b = false;
    
    }
}
