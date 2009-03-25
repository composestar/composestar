
public class T1420catch21 {
    T1420catch21 (){}
    public static void main(String[] args) {
        
        final Exception e = new ClassNotFoundException();
        try {
            throw e;
        } catch (ClassNotFoundException c) {
            // this one will be called
        } catch (Exception ex) {
            // this one is reachable, but will never be executed,
            // since analysis does not evaluate variable contents
        }
    
    }
}
