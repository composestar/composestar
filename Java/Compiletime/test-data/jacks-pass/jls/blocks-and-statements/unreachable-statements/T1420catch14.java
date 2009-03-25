
public class T1420catch14 {
    T1420catch14 (){}
    public static void main(String[] args) {
        
        Exception e = new Exception(); // create here to make test work
        try {
            try {
                throw e;
            } finally {
                return; // discards Exception e
            }
        } catch (Exception e1) {
            // but this is unchecked
        }
    
    }
}
