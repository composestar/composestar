
class Super {
    Super() {
        System.out.print("2 ");
    }
    { System.out.print("1 "); }
}
class T1594rc4 extends Super {
    { System.out.print("3 "); }
    T1594rc4() {
        System.out.print("5");
    }
    public static void main(String[] args) {
        new T1594rc4();
    }
    { System.out.print("4 "); }
}
    