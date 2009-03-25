
public class T1516s5 {
    T1516s5 (){}
    public static void main(String[] args) {
        
	// Because (String) is followed by +, it is an expression not a cast
	final int String = 1;
	switch (String) {
	    case 0:
	    case ((String) + 1 + "") == "2" ? 1 : 0:
	}
    
    }
}
