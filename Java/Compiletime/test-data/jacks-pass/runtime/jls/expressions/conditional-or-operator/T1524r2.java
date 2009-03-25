
class T1524r2 {
    public static void main(String[] args) {
        Object s = new Boolean(true);
        if (!(s instanceof String) || ((String)s).startsWith("t"));
        System.out.print("OK");
    }
}
    