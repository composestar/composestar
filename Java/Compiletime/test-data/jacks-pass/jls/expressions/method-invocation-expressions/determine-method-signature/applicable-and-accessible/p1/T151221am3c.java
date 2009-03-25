
package p1;
abstract class T151221am3a {
    abstract void m(); // note non-public accessibility
}
interface T151221am3b {
    void m() throws Exception;
}
public abstract class T151221am3c extends T151221am3a implements T151221am3b {
    // inherits 2 versions of m()
}
    