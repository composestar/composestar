
package p1;
public class T8462h6a {
    void m() {}
}
class T8462h6c extends p2.T8462h6b {
    // if this were not static, it would override a.m. Therefore, there is
    // a conflict, and this static method is hiding an instance method, even
    // though a.m is not inherited
    protected static void m() {}
}
    