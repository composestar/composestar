
public class T1420catch1 {
    T1420catch1 (){}
    public static void main(String[] args) {
        
        try { // empty block cannot throw exception
        } catch (Throwable t) {
            // but Throwable is superclass of asynchronous exceptions
        }
    
    }
}
