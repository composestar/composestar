
class T1410o1 {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            System.out.print(m(i));
        }
    }
    static int m(int i) {
        switch (i) {
            case 0:
            return 0;
            case 4:
            return 4;
            default:
            return i;
        }
    }
}
    