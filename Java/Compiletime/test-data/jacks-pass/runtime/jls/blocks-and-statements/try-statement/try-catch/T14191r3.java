
class T14191r3 {
    public static void main(String[] args) {
	System.out.print(1);
	try {
	} catch (Error e) {
	}
	System.out.print(2);
	try {
	} catch (Error e) {
	}
	System.out.print(3);
    }
}
    