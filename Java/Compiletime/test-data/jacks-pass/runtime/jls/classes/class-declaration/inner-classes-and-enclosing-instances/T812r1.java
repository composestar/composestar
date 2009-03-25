
class T812r1 {
    public static void main(String[] args) {
        new T812r1().foo(1);
    }
    void foo(final int i) {
        class Local {
            Local() {}
            Local(int i) { this(); }
            int foo() {
                return new Local(0) {
                    int j = i;
                }.j;
            }
        }
        System.out.print(new Local().foo());
    }
}
