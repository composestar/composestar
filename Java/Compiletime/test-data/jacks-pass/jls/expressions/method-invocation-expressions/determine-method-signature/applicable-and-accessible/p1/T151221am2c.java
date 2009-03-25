
package p1;
abstract class T151221am2a {
    abstract void m(); // note non-public accessibility
}
interface T151221am2b {
    void m() throws Exception;
}
public abstract class T151221am2c extends T151221am2a implements T151221am2b {
    // inherits 2 versions of m()
}
    