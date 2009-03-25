
package p1;
public class T8463d11a {
    final void m() {}
}
class T8463d11c extends p2.T8463d11b {
    // can't override final a.m, even though not inherited
    public void m() {}
}
    