
package p1;
public class T6541s11a {
    public class p1 {}
}
class T6541s11c extends p2.T6541s11b {
    // class a.p1 was not inherited, although it is accessible
    // p1 resolves to a package
    p1.T6541s11c p;
}
    