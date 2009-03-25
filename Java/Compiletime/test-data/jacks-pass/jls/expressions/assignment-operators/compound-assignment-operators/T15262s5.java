
public class T15262s5 {
    T15262s5 (){}
    public static void main(String[] args) {
        
	String s = "";
	s += true + s;
	s += 1 + s;
	s += '1' + s;
	s += 1L + s;
	s += 1.0f + s;
	s += 1.0 + s;
    
    }
}
