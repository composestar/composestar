
package p1;
public abstract class T8464m11a {
    abstract int m();
}
abstract class T8464m11c extends p2.T8464m11b {
    // inherited b.m() is concrete, but does not implement a.m()
    int i = m();
}
    