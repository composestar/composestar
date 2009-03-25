
class T131rm2a {
    static void m() {
        // should be invokevirtual T131rm2b.toString; b declares
        System.out.print(((T131rm2b) new T131rm2c()).toString());
    }
}
class T131rm2b {
    public String toString() {
        return "0";
    }
}
class T131rm2c extends T131rm2b {
    public String toString() {
        return "1";
    }
}
    