
package p1;
public class T8464m8a {
    final int m() { return 1; }
}
abstract class T8464m8c extends p2.T8464m8b {
    // inherited abstract b.m() can be implemented outside package p1.
    // Therefore, even though accessible a.m() is final, it need not provide
    // the implementation of b.m() since it is not inherited
    // Note, however, that no concrete subclass of b can live in p1.
    int i = m();
}
    