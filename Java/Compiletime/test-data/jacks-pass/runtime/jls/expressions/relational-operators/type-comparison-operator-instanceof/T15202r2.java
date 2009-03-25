
class T15202r2 {
    static int m() {
	return -1;
    }
    public static void main(String[] args) {
	try {
	    System.out.print(new int[m()] instanceof int[]);
	} catch (NegativeArraySizeException e) {
	    System.out.print("OK");
	}
    }
}
    