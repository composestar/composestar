
package p1;
public class T8464m6a {
    int m() throws Exception { return 1; }
}
class T8464m6c extends p2.T8464m6b {
    // inherited of b.m() does not clash with throws clause of accessible a.m()
    int i = m();
}
    