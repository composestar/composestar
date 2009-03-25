
package p1;
public class T8464mr1a {
    int m() { return 1; }
    public static void main(String[] args) {
	T8464mr1c c = new T8464mr1c();
	T8464mr1a a = c;
	System.out.print(a.m() + " " + c.m());
    }
}
class T8464mr1c extends p2.T8464mr1b {
    // inherits b.m(), but it does not override a.m()
}
    