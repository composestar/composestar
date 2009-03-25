
class T15111rs4 {
    static T15111rs4 s;
    static String str;
    T15111rs4 i;
    T15111rs4() { throw new IllegalArgumentException(); }
    public static void main(String[] args) {
	try {
	    s.i.str = new T15111rs4().toString();
	    System.out.print('a');
	} catch (NullPointerException e) {
	    System.out.print('O');
	} catch (IllegalArgumentException e) {
	    System.out.print('b');
	}
	try {
	    s.i.str += new T15111rs4();
	    System.out.print('c');
	} catch (NullPointerException e) {
	    System.out.print('K');
	} catch (IllegalArgumentException e) {
	    System.out.print('d');
	}
    }
}
    