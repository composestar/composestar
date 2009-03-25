
public class T15181b2 {
    T15181b2 (){}
    public static void main(String[] args) {
        
        String s1 = "" + true;
        String s2 = "" + false;
	s1 = true + "";
	s2 = false + "";
    
    }
}
