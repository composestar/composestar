
package p2;
public abstract class T8111da25b extends p1.T8111da25a {
    // the change in signature is legal, since a.m is not inherited
    abstract int m() throws Exception;
}
class T8111da25d extends p1.T8111da25c {
    // With the implementation of b.m, there are no abstract methods in d
    int m() throws Exception { return 1; }
}
    