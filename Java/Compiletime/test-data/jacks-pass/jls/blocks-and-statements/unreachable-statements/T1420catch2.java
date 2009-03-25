
public class T1420catch2 {
    T1420catch2 (){}
    public static void main(String[] args) {
        
        try {
            int i = 0; // throws no exceptions
            i /= i; // can only throw ArithmeticException
        } catch (ClassCastException e) {
            // but ClassCastException is unchecked
        }
    
    }
}
