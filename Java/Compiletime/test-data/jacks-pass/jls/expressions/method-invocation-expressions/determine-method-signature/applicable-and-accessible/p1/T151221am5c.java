
package p1;
abstract class T151221am5a {
    abstract void m(); // note non-public accessibility
}
interface T151221am5b {
    void m() throws Exception;
}
public abstract class T151221am5c extends T151221am5a implements T151221am5b {
    // inherits 2 versions of m()
}
    