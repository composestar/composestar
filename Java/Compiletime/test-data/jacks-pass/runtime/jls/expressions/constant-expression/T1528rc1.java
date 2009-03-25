
class T1528rc1 {
    static String blah() { return "foo" + "bar"; }
    public static void main(String[] args) {
       System.out.print("foobar" == blah());
    }
}
    