
package p2;
public class T15123rm6b extends p1.T15123rm6a {
    public static void main(String[] args) {
	final T15123rm6d d = new T15123rm6d();
	final p1.T15123rm6c c = d;
	c.m();
	System.out.print(' ');
	d.m();
	System.out.print(' ');
	new Object() {
	    {
		c.m();
		System.out.print(' ');
		d.m();
	    }
	};
    }
}
class T15123rm6d extends p1.T15123rm6c {
    protected void m() {
	System.out.print(4);
    }
}
    