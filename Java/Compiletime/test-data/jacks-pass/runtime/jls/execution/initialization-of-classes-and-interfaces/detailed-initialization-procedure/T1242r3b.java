
class T1242r3a {
    static {
        T1242r3b.i = 1;
        T1242r3b.s = "oops";
    }
}
class T1242r3b extends T1242r3a {
    static int i = 0;
    static String s = null;
    public static void main(String[] args) {
        System.out.print(i + " " + s);
    }
}
