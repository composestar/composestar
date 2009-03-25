
public class T1613dup5 {
    T1613dup5 (){}
    public static void main(String[] args) {
        
        final boolean x;
        boolean y = true;
        y = false || y;
        x = y;
    
    }
}
