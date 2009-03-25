
class T1348cr2a {
    static void peek() { System.out.print(T1348cr2b.i); }
    { System.out.print(((T1348cr2b) this).j); }
    public static void main(String[] args) {
        new T1348cr2b();
    }
}
class T1348cr2b extends T1348cr2a {
    static { peek(); }
    static int i = 1;
    int j = 2;
}
    