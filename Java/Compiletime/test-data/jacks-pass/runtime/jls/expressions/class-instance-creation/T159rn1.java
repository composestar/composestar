
class T159rn1 {

    private static class Inner {
        public Inner() {
            System.err.print("ctor");
        }

        private void foo() {}
    }

    public static void main(String[] args) {
        new Inner().foo();              // broken in Jikes 1.11
        System.err.print(" -> ");
        (new Inner()).foo();            // okay
    }
}
    