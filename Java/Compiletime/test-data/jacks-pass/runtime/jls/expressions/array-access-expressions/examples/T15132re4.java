
class T15132re4 {
    public static void main(String[] args) {
	int[] a = null;
	try {
	    int i = a[vamoose()];
	    System.out.print(i);
	} catch (NullPointerException e) {
	} catch (Exception e) {
	    System.out.print("OK");
	}
    }
    static int vamoose() throws Exception {
	throw new Exception("Twenty-three skidoo!");
    }
}
    