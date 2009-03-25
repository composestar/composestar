
package p1;
public class T6541s10a {
    class p1 {}
}
class T6541s10c extends p2.T6541s10b {
    // class p1 was not inherited, although it is accessible
    // p1 resolves to a package
    p1.T6541s10c p;
}
    