
public class T1524o2 {
    T1524o2 (){}
    public static void main(String[] args) {
        
	boolean b = true;
	if (true || b);
	if (true || !b);
	b = false;
    
    }
}
