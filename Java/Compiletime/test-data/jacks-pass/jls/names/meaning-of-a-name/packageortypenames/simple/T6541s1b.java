
class T6541s1b {
    static class p1 {
        interface T6541s1a {}
    }
    // p1 is a type, not a package, so p1.T6541s1a is an interface, not a class
    class Sub implements p1.T6541s1a {}
}
    