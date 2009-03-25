
package p1;
public class T8463d14a {
    public class E1 extends Exception {}
    public class E2 extends Exception {}
    static void m() throws E1 {}
}
class T8463d14c extends p2.T8463d14b {
    // conflicting throws clause, even though a.m() not inherited
    public static void m() throws E2 {}
}
    