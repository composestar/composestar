
class T885ri3 {
    class Inner {
        Inner() {}
        Inner(int i) {}
    }
    public static void main(String[] args) {
        new T885ri3().new Inner(1);
        System.out.print("OK");
    }
}
    