
class T165r3 {
    public static void main(String[] args) {
	new T165r3(1);
    }
    T165r3(final int i) {
	class L {
	    L() {
		System.out.print("a ");
	    }
	    L(int j) {
		this(new L() {
		    { System.out.print("b" + i + ' '); }
		});
		System.out.print("d" + i + ' ');
            }
	    L(Object o) {
		System.out.print("c ");
	    }
	}
	new L(i) {
	    { System.out.print("e" + i); }
	};
    }
}
    