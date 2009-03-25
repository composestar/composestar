
class T131rm4a {
    static void m() {
        // should be invokeinterface T131rm4b.toString; b declares
        System.out.print(((T131rm4b) new T131rm4c()).toString());
    }
}
interface T131rm4b {
    String toString();
}
class T131rm4c implements T131rm4b {
    public String toString() {
        return "1";
    }
}
    