
package p1;
public class T8463d5a {
    final void m() {}
}
class T8463d5c extends p2.T8463d5b {
    // can't override final a.m, even though not inherited
    void m() {}
}
    