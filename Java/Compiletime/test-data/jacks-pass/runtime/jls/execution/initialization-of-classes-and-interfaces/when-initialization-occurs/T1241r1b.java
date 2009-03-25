
class T1241r1a {
    final int i = 1;
    static final int j = 1;
    static { System.out.print("3 "); }
}
class T1241r1b {
    public static void main(String[] args) {
	T1241r1a a = null;
	System.out.print(a.j + " ");
	try {
	    System.out.print(a.i);
	} catch (Exception e) {
	    System.out.print("2 ");
	    a = new T1241r1a();
	    System.out.print(a.i + 3);
	}
    }
}
    