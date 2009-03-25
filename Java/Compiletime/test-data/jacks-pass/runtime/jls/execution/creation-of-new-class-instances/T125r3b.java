
class T125r3a {
    T125r3a() { m(); }
    void m() {}
}
class T125r3b extends T125r3a {
    int i = 0;
    String s = null;
    void m() {
        i = 1;
        s = "oops";
    }
    public static void main(String[] args) {
        T125r3b t = new T125r3b();
        System.out.print(t.i + " " + t.s);
    }
}
