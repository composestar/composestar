
package p1;
public class T8463d3a {
    void m() {}
}
class T8463d3c extends p2.T8463d3b {
    // conflicting throws clause, even though a.m() not inherited
    void m() throws Exception {}
}
    