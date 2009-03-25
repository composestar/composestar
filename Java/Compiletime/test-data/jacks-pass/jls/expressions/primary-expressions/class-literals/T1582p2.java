
public class T1582p2 {
    T1582p2 (){}
    public static void main(String[] args) {
        
	try {
	    Object.class.forName("java.lang.Object");
	} catch (Exception e) {
	}
    
    }
}
