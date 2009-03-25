
import java.util.*;
class T1491r1 {
    static Set s = new HashSet();
    public static void main(String[] args) {
	if (null == s)
            System.out.print("oops");
	System.out.print(null == s ? "oops" : "OK");
    }
}
    