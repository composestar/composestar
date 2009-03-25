
class T15111rs3 {
    public String toString() { throw new IllegalArgumentException(); }
    public static void main(String[] args) {
	try { // string conversion will fail
	    java.util.Comparator c =
	        ("foo" + new T15111rs3()).CASE_INSENSITIVE_ORDER;
	    System.out.print("Oops ");
	} catch (IllegalArgumentException e) {
	    System.out.print("OK");
	}
    }
}
    