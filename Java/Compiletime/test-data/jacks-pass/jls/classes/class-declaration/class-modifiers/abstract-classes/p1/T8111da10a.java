
package p1;
public abstract class T8111da10a {
    abstract void m();
}
class T8111da10c extends p2.T8111da10b {
    void m() {} // overrides a.m(), even though a.m() is not inherited!
}
    