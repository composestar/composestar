
interface T151222a8b {
    void m(String s, Object o);
}
abstract class T151222a8c extends p1.T151222a8a implements T151222a8b {}
abstract class T151222a8d extends T151222a8c {
    // Even though d inherits two versions of m, the protected a.m
    // is only accessible if the qualifying expression is type d or lower
    void foo(T151222a8c c) {
        c.m("", "");
    } // only b.m(String, Object) is accessible
}
    