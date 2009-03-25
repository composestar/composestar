
class T883ri4 {
    class Inner {
        private Inner() {}
    }
    public static void main(String[] args) {
        new T883ri4().new Inner();
        System.out.print("OK");
    }
}
    