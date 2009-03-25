
class T885ri2 {
    class Inner {
        Inner(int i) {}
    }
    public static void main(String[] args) {
        new T885ri2().new Inner(1);
        System.out.print("OK");
    }
}
    