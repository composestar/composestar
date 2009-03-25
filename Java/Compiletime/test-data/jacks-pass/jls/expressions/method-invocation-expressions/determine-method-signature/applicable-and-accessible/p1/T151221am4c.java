
package p1;
abstract class T151221am4a {
    abstract void m(); // note non-public accessibility
}
interface T151221am4b {
    void m() throws Exception;
}
public abstract class T151221am4c extends T151221am4a implements T151221am4b {
    // inherits 2 versions of m()
}
    