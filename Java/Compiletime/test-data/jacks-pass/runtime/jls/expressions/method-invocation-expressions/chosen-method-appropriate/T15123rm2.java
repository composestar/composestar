
class T15123rm2 {
    static class One {
	private static void m() {
	    System.out.print(1);
	}
    }
    static class Two extends One {
	static void m() {
	    System.out.print(2);
	}
    }
    public static void main(String[] args) {
	One o = new Two();
	o.m(); // invokes One.m(), even though it is an instance of Two
    }
}
    