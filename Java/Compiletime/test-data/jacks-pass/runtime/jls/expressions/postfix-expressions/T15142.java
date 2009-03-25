
class T15142 {
    public static void main(String[] args) {
        int a = 2, b = 2;
        System.out.print((-a-- == -2) + " ");
        System.out.print((-(b--) == -2) + " ");
        System.out.print(a == b);
    }
}
    