
package p1;
public class T8464m4a {
    void m() {}
}
class T8464m4c extends p2.T8464m4b {
    // inherited return type of b.m() does not clash with accessible a.m()
    int i = m();
}
    