
public class T15202s2 {
    T15202s2 (){}
    public static void main(String[] args) {
        
	Object o = "";
	// narrowing
	boolean b = o instanceof Integer; // false at runtime
    
    }
}
