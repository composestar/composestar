
class T887rad1 {
    private T887rad1() {
        System.out.print("super ");
    }
    static class Static extends T887rad1 {}
    class Inner extends T887rad1 {}
    void foo() {
        class InnerLocal extends T887rad1 {}
        new InnerLocal();
        new T887rad1() {};
    }
    static void bar() {
        class StaticLocal extends T887rad1 {}
        new StaticLocal();
        new T887rad1() {};
    }
    public static void main(String[] args) {
        T887rad1 t = new Static();
        t.new Inner();
        t.foo();
        bar();
        System.out.print("main");
    }
}
    