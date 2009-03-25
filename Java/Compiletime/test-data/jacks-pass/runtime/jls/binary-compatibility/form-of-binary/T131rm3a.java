
class T131rm3a {
    static void m() {
        // should be invokevirtual Object.toString; b doesn't declare
        System.out.print(((T131rm3b) new T131rm3c()).toString());
    }
}
interface T131rm3b {}
class T131rm3c implements T131rm3b {
    public String toString() {
        return "1";
    }
}
    