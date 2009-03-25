
public class T1420catch16 {
    T1420catch16 (){}
    public static void main(String[] args) {
        
        Exception e = new Exception(); // create here to make test work
        try {
            try {
                throw e;
            } catch (Exception e1) {
                throw e1;
            } finally {
                return; // discards Exception e1
            }
        } catch (Exception e2) {
            // but Exception is superclass of asynchronous exceptions
        }
    
    }
}
