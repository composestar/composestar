
class T1348cr2b extends T1348cr2a {
    static { peek(); }
    static final int i = 1; // now final
    final int j = 2; // now final
}
class T1348cr2c {
    public static void main(String[] args) {
        T1348cr2a.main(args);
    }
}
    