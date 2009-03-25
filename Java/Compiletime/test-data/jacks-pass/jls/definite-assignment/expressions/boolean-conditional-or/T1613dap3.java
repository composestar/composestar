
public class T1613dap3 {
    T1613dap3 (){}
    public static void main(String[] args) {
        
        boolean x, y = false;
        if (false || (false ? y : false))
            y = x;
    
    }
}
