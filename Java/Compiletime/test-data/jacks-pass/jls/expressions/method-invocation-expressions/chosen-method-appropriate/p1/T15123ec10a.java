
package p1;
public class T15123ec10a {
    int m() { return 1; }
    class C extends p2.T15123ec10b {
	C(int j) {}
	C() {
	    // c does not inherit m(), so use the enclosing version
	    this(m());
	}
    }
}
    