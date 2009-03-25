
public class T1417t13 {
    T1417t13 (){}
    public static void main(String[] args) {
        
        try {
            throw (Throwable) new Object(); // legal, throws ClassCastException
        } catch (Throwable t) {
        }
    
    }
}
