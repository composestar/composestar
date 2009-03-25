
class T1515r4 {
    static byte s = -128; // static field
    byte i = -128; // instance field
    public static void main(String[] args) {
        new T1515r4();
    }
    T1515r4() {
        byte[] a = {-128}; // array access
        byte l = -128; // local variable
        System.out.print(--s + " " + --i + " " + --a[0] + " " + --l);
        System.out.print(" " + s + " " + i + " " + a[0] + " " + l);
    }
}
    