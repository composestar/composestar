
class T883ri2 {
    class Inner {
        public Inner() {}
    }
    public static void main(String[] args) {
        new T883ri2().new Inner();
        System.out.print("OK");
    }
}
    