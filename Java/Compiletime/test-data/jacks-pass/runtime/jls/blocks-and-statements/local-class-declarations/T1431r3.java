
class T1431r3 {
    static class Super {
	Object shared;
	Super(Object o) { shared = o; }
    }
    public static void main(String[] args) {
	final Object o = new Object();
	class Local extends Super {
	    Local() {
		super(o);
	    }
	}
	Local a = new Local();
	Local b = new Local();
	System.out.print(a.shared == o && b.shared == o);
    }
}
    