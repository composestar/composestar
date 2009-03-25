
class T15141 {
    public static void main(String[] args) {
        int a = 1, b = 1;
        System.out.print((-a++ == -1) + " ");
        System.out.print((-(b++) == -1) + " ");
        System.out.print(a == b);
    }
}
    