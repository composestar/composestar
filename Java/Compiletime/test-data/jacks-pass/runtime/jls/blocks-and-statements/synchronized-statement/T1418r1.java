
class T1418r1 {
    public static void main(String[] args) {
	m();
    }
    static void m() {
	synchronized ("") {
	    int i = 1;
	}
	synchronized ("") {
	    System.out.print("OK");
	}
    }
}
    