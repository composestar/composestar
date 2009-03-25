
class T15132re3 {
    public static void main(String[] args) {
	int index = 1;
	try {
	    nada()[index = 2]++;
	} catch (Exception e) {
	    System.out.print(index);
	}
    }
    static int[] nada() { return null; }
}
    