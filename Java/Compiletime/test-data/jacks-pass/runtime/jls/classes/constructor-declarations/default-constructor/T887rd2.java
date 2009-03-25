
class  T887rd2_super {
    T887rd2_super() { System.out.print("super "); }
}
public class T887rd2 extends T887rd2_super {
    public static void main(String[] argv) {
        new T887rd2().toString();
        (new T887rd2()).toString();
        System.out.print("main");
    }
}
    