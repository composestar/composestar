
class T15132re2 {
    public static void main(String[] args) {
	int index = 1;
	try {
	    skedaddle()[index = 2]++;
	} catch (Exception e) {
	    System.out.print(index);
	}
    }
    static int[] skedaddle() throws Exception {
	throw new Exception("Ciao");
    }
}
    