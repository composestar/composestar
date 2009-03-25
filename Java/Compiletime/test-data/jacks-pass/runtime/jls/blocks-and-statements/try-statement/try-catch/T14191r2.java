
class T14191r2 {
    public static void main(final String[] args) {
	RuntimeException e = new RuntimeException();
	try {
	    foo(e);
	    System.out.print("exception not thrown");
	} catch (Exception ex) {
	    System.out.print(ex == e ? "OK" : "wrong exception thrown");
	}
    }
    static int foo(RuntimeException o) {
	synchronized (o) {
	    try {
		return bar();
	    } catch (RuntimeException e) {
		throw o;
	    }
	}
    }
    static int bar() { throw new RuntimeException(); }
}
    