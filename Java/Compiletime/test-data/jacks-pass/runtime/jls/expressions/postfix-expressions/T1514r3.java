
class T1514r3 {
    static byte s = 127; // static field
    byte i = 127; // instance field
    public static void main(String[] args) {
        new T1514r3();
    }
    T1514r3() {
        byte[] a = {127}; // array access
        byte l = 127; // local variable
        System.out.print(s++ + " " + i++ + " " + a[0]++ + " " + l++);
        System.out.print(" " + s + " " + i + " " + a[0] + " " + l);
    }
}
    