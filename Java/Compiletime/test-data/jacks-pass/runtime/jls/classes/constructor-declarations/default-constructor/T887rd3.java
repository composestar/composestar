
class  T887rd3_super {
    T887rd3_super() { System.out.print("super "); }
}
public class T887rd3 {

    private static class Inner extends T887rd3_super {}

    public static void main(String[] argv) {
        new Inner().toString();
        System.out.print("+ ");
        (new Inner()).toString();
        System.out.print("main");
    }
}
    