
class T151222a21b extends p1.T151222a21a {
    // Even though b inherits two versions of m, the protected a.m
    // is only accessible if the qualifying expression is type b or lower
    void foo(p1.T151222a21a t) {
        t.m("", "");
    } // only a.m(String, Object) is accessible
}
    