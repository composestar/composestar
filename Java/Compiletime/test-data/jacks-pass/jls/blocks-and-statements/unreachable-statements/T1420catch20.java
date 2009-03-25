
public class T1420catch20 {
    T1420catch20 (){}
    public static void main(String[] args) {
        
        class MyException extends ArithmeticException {}
        try {
            int i = 0;
            i /= i; // throws only ArithmeticException, not MyException
        } catch (MyException e) {
            // but this is unchecked
        }
    
    }
}
