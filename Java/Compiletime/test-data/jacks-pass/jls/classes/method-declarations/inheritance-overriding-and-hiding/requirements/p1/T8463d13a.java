
package p1;
public class T8463d13a {
    public class E1 extends Exception {}
    public class E2 extends Exception {}
    void m() throws E1 {}
}
class T8463d13c extends p2.T8463d13b {
    // conflicting throws clause, even though a.m() not inherited
    public void m() throws E2 {}
}
    