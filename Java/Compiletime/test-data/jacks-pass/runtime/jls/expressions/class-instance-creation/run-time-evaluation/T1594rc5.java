
class T1594rc5 {
    class Inner {
	Inner(int i) {}
    }
    public static void main(String[] args) {
	int i = 1;
	try {
	    T1594rc5 t = null;
	    t.new Inner(i++) {};
	} catch (NullPointerException e) {
	    System.out.print(i + " ");
	}
	try {
	    ((T1594rc5) null).new Inner(i++) {};
	} catch (NullPointerException e) {
	    System.out.print(i);
	}
    }
}
    