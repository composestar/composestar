
class T14191r1 {
    public static void main(String[] o) {
	synchronized (o) {
	    try {
		throw new Error();
	    } catch (Error e) {
		System.out.print("OK");
		return;
	    }
	}
    }
}
    