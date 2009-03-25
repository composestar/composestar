
package p1;
public class T8461o6a {
    static void m() {}
}
class T8461o6c extends p2.T8461o6b {
    // if a.m were not static, this would override it. Therefore, there is
    // a conflict, and this instance method is overriding a static method, even
    // though a.m is not inherited
    protected void m() {}
}
    