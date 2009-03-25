
class E1 extends Exception {}
class E2 extends Exception {}
class E3 extends Exception {}
abstract class T151222a11a {
    public abstract void m() throws E1, E2;
}
interface T151222a11b {
    void m() throws E2, E3;
}
abstract class T151222a11c extends T151222a11a implements T151222a11b {
    {
        try {
            m(); // whether a.m() or b.m() is chosen, it cannot throw E1 or E3
        } catch (E2 e2) {
        } catch (E3 e3) {
        }
    }
}
    