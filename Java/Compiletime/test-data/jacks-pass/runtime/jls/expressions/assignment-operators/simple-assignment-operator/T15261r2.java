
class T15261r2 {
    public static void main(String[] args) {
	try {
	    // prior to JDK 1.5, the compiler must insert a checkcast when
	    // using null as a multi-dimensional array type
	    ((Object[]) null)[0] = new int[0];
	} catch (NullPointerException e) {
	    System.out.print('A');
	}
	try {
	    // prior to JDK 1.5, the compiler must insert a checkcast when
	    // assigning null to a multi-dimensional array type local
	    java.io.Serializable[] i = null;
	    i[0] = new int[0];
	} catch (NullPointerException e) {
	    System.out.print('O');
	}
	try {
	    Cloneable[] i;
	    i = null;
	    i[0] = new int[0];
	} catch (NullPointerException e) {
	    System.out.print('K');
	}
    }
}
    