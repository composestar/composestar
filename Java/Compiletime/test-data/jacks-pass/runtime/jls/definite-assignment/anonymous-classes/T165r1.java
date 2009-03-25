
class T165r1 {
    T165r1(int j) {}
    public static void main(String[] args) {
        final int i;
        new T165r1(i = 1) {
            { System.out.print(i); }
        };
    }
}
    