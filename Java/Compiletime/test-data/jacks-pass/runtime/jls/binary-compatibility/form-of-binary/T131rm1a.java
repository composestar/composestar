
class T131rm1a {
    static void m() {
        // should be invokevirtual Object.toString; b doesn't declare
        System.out.print(((T131rm1b) new T131rm1c()).toString());
    }
}
class T131rm1b {}
class T131rm1c extends T131rm1b {
    public String toString() {
        return "1";
    }
}
    