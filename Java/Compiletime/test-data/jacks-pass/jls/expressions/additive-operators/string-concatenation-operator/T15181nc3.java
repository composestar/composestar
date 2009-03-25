
public class T15181nc3 {
    T15181nc3 (){}
    public static void main(String[] args) {
        
        String s1 = "C";
        String s2 = "A" + ("B" + s1);
	s2 = ("B" + s1) + "A";
	s2 = (s1 + "B") + "A";
    
    }
}
