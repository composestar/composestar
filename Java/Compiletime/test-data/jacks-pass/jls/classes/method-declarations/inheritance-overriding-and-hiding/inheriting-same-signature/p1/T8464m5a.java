
package p1;
public class T8464m5a {
    public class E extends Exception {}
    void m() {}
}
class T8464m5c extends p2.T8464m5b {
    // inherited throws clause of b.m() does not clash with accessible a.m()
    {
	try {
	    m();
	} catch (E e) {
	}
    }
}
    