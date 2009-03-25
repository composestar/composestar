
class T1525r5 {
    public static void main(String[] args) {
	boolean b = false;
	if ((b = true) ? true : true)
            System.out.print(b);
    }
}
    