
class T15152 {
    public static void main(String[] args) {
        int a = 3, b = 3;
        System.out.print((--a*a == 4) + " ");
        System.out.print(((--b)*b == 4) + " ");
        System.out.print(a == b);
    }
}
    