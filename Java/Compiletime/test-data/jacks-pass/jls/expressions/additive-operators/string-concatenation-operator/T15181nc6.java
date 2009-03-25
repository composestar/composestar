
public class T15181nc6 {
    T15181nc6 (){}
    public static void main(String[] args) {
        
        String s1 = "C";
        int n = 10;
        String s2 = n + ("B" + s1);
	s2 = ("B" + s1) + n;
	s2 = (s1 + "B") + n;
    
    }
}
