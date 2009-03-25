
class T1525r1 {
    public static void main(String[] args) {
	boolean b = false;
	System.out.print(((b = true) ? null : null) + " " + b);
    }
}
    