
class Super {
    Super(int x) {
        method(x);
    }

    void method(int x) { }
}
class T125r1 extends Super {
    private int i1 = 1;
    private Integer i2 = new Integer(1);
    void method(int x) {
        i1 = x;
        i2 = new Integer(x);
        System.out.print("A ");
    }
    T125r1() {
        super(2); // superclass calls method()
    }
    public static void main(String[] args) {
        T125r1 t = new T125r1();
        System.out.print(t.i1 + " " + t.i2);
    }
}
    