
interface T151222a7b {
    void m(String s, Object o);
}
abstract class T151222a7c extends p1.T151222a7a implements T151222a7b {
    { m("", ""); } // only b.m(String, Object) is accessible
}
    