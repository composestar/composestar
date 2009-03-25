
class T15151 {
    public static void main(String[] args) {
        int a = 1, b = 1;
        System.out.print((++a*a == 4) + " ");
        System.out.print(((++b)*b == 4) + " ");
        System.out.print(a == b);
    }
}
    