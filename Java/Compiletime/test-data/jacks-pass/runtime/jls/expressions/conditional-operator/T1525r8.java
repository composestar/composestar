
class T1525r8 {
    public static void main(String[] args) {
	boolean b;
	boolean x = (args.length > 0 || (b = false)) ? true : b;
	System.out.print(x);
    }
}
    