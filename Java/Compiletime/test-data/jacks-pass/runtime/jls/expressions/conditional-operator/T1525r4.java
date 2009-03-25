
class T1525r4 {
    public static void main(String[] args) {
	boolean b = true;
	System.out.print(((b = false) ? -0. : 0) + " " + b);
    }
}
    