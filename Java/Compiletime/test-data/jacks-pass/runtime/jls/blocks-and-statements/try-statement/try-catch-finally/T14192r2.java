
class T14192r2 {
    public static void main(final String[] args) {
	try {
	    foo(args);
	    System.out.print("exception not thrown");
	} catch (Exception e) {
	    System.out.print("2");
	}
    }
    static int foo(Object o) {
	synchronized (o) {
	    try {
		return bar();
	    } finally {
		System.out.print("1 ");
	    }
	}
    }
    static int bar() { throw new RuntimeException(); }
}
    