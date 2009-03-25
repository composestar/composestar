
class T1525r2 {
    public static void main(String[] args) {
	boolean b = false;
	System.out.print(((b = true) ? 1 : 1) + " " + b);
    }
}
    