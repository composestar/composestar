
package p1;
public class T8463d2a {
    static void m() {}
}
class T8463d2c extends p2.T8463d2b {
    // conflicting return type, even though a.m() not inherited
    static int m() { return 1; }
}
    