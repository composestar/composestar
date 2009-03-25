
public class T15181nc5 {
    T15181nc5 (){}
    public static void main(String[] args) {
        
        String s1 = "C";
        int n = 10;
        String s2 = "A" + (n + s1);
	s2 = (n + s1) + "A";
	s2 = (s1 + n) + "A";
    
    }
}
