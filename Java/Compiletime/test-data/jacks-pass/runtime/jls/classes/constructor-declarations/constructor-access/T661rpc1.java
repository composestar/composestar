
public class T661rpc1 {
class T661rpc1_innersuper {
    private T661rpc1_innersuper() { }
}
class T661rpc1_inner extends T661rpc1_innersuper {
    T661rpc1_inner() { super(); }
}
public static void main(String[] args) {
    new T661rpc1().new T661rpc1_inner();
    System.out.print("OK");
}
}
    