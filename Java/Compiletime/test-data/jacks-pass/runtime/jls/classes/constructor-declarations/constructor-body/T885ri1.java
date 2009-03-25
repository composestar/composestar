
class T885ri1 {
    class Inner {
        Inner() {}
    }
    public static void main(String[] args) {
        new T885ri1().new Inner();
        System.out.print("OK");
    }
}
    