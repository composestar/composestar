
class T885ri4 {
    class Inner {
        Inner() {}
        Inner(int i) { this(); }
    }
    public static void main(String[] args) {
        new T885ri4().new Inner(1);
        System.out.print("OK");
    }
}
    