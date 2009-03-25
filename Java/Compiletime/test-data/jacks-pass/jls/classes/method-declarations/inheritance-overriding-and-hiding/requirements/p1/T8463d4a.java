
package p1;
public class T8463d4a {
    static void m() {}
}
class T8463d4c extends p2.T8463d4b {
    // conflicting throws clause, even though a.m() not inherited
    static void m() throws Exception {}
}
    