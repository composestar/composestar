
public class T1420catch18 {
    T1420catch18 (){}
    public static void main(String[] args) {
        
        try {
            try {
                throw new Exception();
            } catch (Throwable t) {
                // traps all exceptions
            }
        } catch (Exception e) {
            // but this is unchecked
        }
    
    }
}
