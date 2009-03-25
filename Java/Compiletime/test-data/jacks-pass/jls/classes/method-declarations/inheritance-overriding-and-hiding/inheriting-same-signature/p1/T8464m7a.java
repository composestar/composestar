
package p1;
public class T8464m7a {
    final int m() { return 1; }
}
class T8464m7c extends p2.T8464m7b {
    // inherited b.m() does not violate final accessible a.m()
    int i = m();
}
    