
package p1;
public class T8463d9a {
    void m() {}
}
class T8463d9c extends p2.T8463d9b {
    // conflicting throws clause, even though a.m() not inherited
    public void m() throws Exception {}
}
    