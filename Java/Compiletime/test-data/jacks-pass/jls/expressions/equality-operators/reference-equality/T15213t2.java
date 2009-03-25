
public class T15213t2 {
    T15213t2 (){}
    public static void main(String[] args) {
        
	boolean b = new Integer(1) == (Object) "";
	b = (Object) new Integer(1) == "";
    
    }
}
