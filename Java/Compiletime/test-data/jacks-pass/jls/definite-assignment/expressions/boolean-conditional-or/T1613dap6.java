
public class T1613dap6 {
    T1613dap6 (){}
    public static void main(String[] args) {
        
        boolean x, y = false;
        y = (x = false) || y;
        y = x;
    
    }
}
