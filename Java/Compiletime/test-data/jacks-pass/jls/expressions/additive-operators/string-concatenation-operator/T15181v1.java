
public class T15181v1 {
    T15181v1 (){}
    public static void main(String[] args) {
        
        boolean b = true;
        String s = "" + (b ? null : null);
	s = (b ? null : null) + "";
    
    }
}
