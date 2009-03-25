
class T1528rc5b {
    public static void main(String[] args) {
        try {
	    new T1528rc5a();
        } catch (ArithmeticException e) {
            System.out.print("OK");
        }
    }
}
    