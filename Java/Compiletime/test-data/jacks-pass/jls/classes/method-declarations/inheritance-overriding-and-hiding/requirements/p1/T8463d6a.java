
package p1;
public class T8463d6a {
    static final void m() {}
}
class T8463d6c extends p2.T8463d6b {
    // can't override final a.m, even though not inherited
    static void m() {}
}
    