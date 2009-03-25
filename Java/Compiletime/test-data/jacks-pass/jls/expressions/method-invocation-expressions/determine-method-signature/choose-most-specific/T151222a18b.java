
interface T151222a18b {
    void m(String s, Object o);
}
abstract class T151222a18c extends p1.T151222a18a implements T151222a18b {}
abstract class T151222a18d extends T151222a18c {
    // Even though d inherits two versions of m, the protected a.m
    // is only accessible if the qualifying expression is type d or lower
    void foo() {
        super.m("", "");
    } // both a.m(Object, String) and b.m(String, Object) are accessible
}
    