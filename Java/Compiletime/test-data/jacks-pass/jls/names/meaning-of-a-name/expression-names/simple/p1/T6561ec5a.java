
package p1;
public class T6561ec5a {
    int i;
    class C extends p2.T6561ec5b {
	C(int j) {}
	C() {
	    // although c is a subclass of a, it does not inherit i, since
	    // its superclass is in a different package
	    this(i);
	}
    }
}
    