
public class T1420catch22 {
    T1420catch22 (){}
    public static void main(String[] args) {
        
        class MyException extends ClassNotFoundException {}
        try {
            throw new MyException();
        } catch (MyException m) {
        } catch (ClassNotFoundException e) {
            // this one is reachable, but will never be executed,
            // as the only exception in the try block has already been caught
        }
    
    }
}
