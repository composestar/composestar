
package p1;
public class T8463d8a {
    static void m() {}
}
class T8463d8c extends p2.T8463d8b {
    // conflicting return type, even though a.m() not inherited
    public static int m() { return 1; }
}
    