
public class T1613dup2 {
    T1613dup2 (){}
    public static void main(String[] args) {
        
        final boolean x;
        if (false || ((x = true) && false))
            x = true;
    
    }
}
