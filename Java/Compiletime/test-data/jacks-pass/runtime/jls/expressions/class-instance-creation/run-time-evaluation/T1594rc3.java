
class T1594rc3 {
    int i = getInt(); // will not be called
    T1594rc3(int a, int b, int c) {}
    static int getInt() {
        System.out.print("Oops ");
        return 1;
    }
    { System.out.print("Bad "); } // will not be called
    static int choke() { throw new RuntimeException(); }
    public static void main(String[] args) {
        int a = 0, b = 0, c = 0;
        try {
            new T1594rc3(a = 1, b = choke(), c = 1);
        } catch (RuntimeException re) {
            if (a == 1 && b == 0 && c == 0)
                System.out.print("OK");
            else
                System.out.print("Out of order");
            return;
        }
        System.out.println("Should not get here");
    }
}
    