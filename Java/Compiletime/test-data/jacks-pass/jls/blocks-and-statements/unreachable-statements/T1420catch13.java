
public class T1420catch13 {
    T1420catch13 (){}
    public static void main(String[] args) {
        
        try {
            return; // throws nothing
        } catch (RuntimeException e) {
            // but this is unchecked
        }
    
    }
}
