
public class T1523o2 {
    T1523o2 (){}
    public static void main(String[] args) {
        
	boolean b = true;
	if (true && b);
	if (true && !b);
	b = false;
    
    }
}
