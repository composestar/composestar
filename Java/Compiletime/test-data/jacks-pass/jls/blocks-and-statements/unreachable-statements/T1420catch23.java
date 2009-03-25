
public class T1420catch23 {
    T1420catch23 (){}
    public static void main(String[] args) {
        
        try {
            new Object();
        } catch (RuntimeException r) {
        } catch (Error er) {
        } catch (Exception e) {
            // this one is reachable, but will never be executed,
            // as the try block can only throw RuntimeException or
            // Error, which have already been caught
        }
    
    }
}
