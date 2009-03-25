
package p1;
public class T8463d7a {
    void m() {}
}
class T8463d7c extends p2.T8463d7b {
    // conflicting return type, even though a.m() not inherited
    public int m() { return 1; }
}
    