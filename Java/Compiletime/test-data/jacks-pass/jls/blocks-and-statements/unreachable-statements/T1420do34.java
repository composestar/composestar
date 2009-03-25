
public class T1420do34 {
    T1420do34 (){}
    public static void main(String[] args) {
        
	a: do try {
	    throw new Exception();
	} catch (Exception e) {
	} finally {
	    continue a;
	} while (false);
	a: do try {
	} finally {
	    continue a;
	} while (false);
        int i = 1;
    
    }
}
