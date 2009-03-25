
class T165r4 {
    public static void main(String[] args) {
	new T165r4(1);
    }
    T165r4(final int i) {
	class L {
	    private L() {
		System.out.print("a ");
	    }
	    private L(int j) {
		this(new L() {
		    { System.out.print("b "); }
		});
		System.out.print("d" + i + ' ');
            }
	    private L(Object o) {
		System.out.print("c ");
	    }
	}
	new L(i) {
	    { System.out.print("e" + i); }
	};
    }
}
    