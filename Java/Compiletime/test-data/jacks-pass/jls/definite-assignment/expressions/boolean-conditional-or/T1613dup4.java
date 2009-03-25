
public class T1613dup4 {
    T1613dup4 (){}
    public static void main(String[] args) {
        
        final boolean x;
        x = false;
        boolean y = true || (x = true);
    
    }
}
