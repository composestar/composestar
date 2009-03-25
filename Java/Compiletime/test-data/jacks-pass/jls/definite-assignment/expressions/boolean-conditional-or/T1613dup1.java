
public class T1613dup1 {
    T1613dup1 (){}
    public static void main(String[] args) {
        
        final boolean x;
        if ((x = false) || true);
        else x = false;
    
    }
}
