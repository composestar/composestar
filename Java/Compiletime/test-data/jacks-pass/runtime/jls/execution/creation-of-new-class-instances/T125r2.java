
class Super {
    Super() { printThree(); }
    void printThree() { System.out.println("three"); }
}
class T125r2 extends Super {
    int three = (int)Math.PI;  // That is, 3
    public static void main(String[] args) {
        T125r2 t = new T125r2();
        t.printThree();
    }
    void printThree() { System.out.print(three); }
}
    