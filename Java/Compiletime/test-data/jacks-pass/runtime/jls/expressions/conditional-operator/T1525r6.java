
class T1525r6 {
    static boolean a() {
	System.out.print('a');
	return false;
    }
    static boolean b() {
	System.out.print('b');
	return false;
    }
    public static void main(String[] args) {
	boolean b = false;
	if (true | (b ? a() : b()))
            System.out.print(1);
	b = true;
	if (false & (b ? a() : b())) {
	    b = false;
	} else System.out.print(2);
    }
}
    