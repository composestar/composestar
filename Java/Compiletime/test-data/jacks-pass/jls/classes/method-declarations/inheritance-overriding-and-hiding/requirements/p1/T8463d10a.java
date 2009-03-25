
package p1;
public class T8463d10a {
    static void m() {}
}
class T8463d10c extends p2.T8463d10b {
    // conflicting throws clause, even though a.m() not inherited
    public static void m() throws Exception {}
}
    