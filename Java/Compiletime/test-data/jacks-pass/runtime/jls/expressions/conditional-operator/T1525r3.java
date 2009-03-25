
class T1525r3 {
    public static void main(String[] args) {
	boolean b = false;
	System.out.print(((b = true) ? "a" : "a") + " " + b);
    }
}
    