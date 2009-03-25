
class T883ri3 {
    class Inner {
        protected Inner() {}
    }
    public static void main(String[] args) {
        new T883ri3().new Inner();
        System.out.print("OK");
    }
}
    