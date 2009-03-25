
package p1;
public class T8463d12a {
    static final void m() {}
}
class T8463d12c extends p2.T8463d12b {
    // can't override final a.m, even though not inherited
    public static void m() {}
}
    