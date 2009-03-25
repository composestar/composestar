
package p1;
public class T8463d1a {
    void m() {}
}
class T8463d1c extends p2.T8463d1b {
    // conflicting return type, even though a.m() not inherited
    int m() { return 1; }
}
    