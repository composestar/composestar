
public class T15181nc2 {
    T15181nc2 (){}
    public static void main(String[] args) {
        
        String s1 = "C";
        String s2 = "A" + "B" + s1;
	s2 = s1 + "A" + "B";
    
    }
}
