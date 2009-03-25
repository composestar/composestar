
public class T1613dup3 {
    T1613dup3 (){}
    public static void main(String[] args) {
        
        final boolean x;
        boolean y = (x = false) || false;
    
    }
}
