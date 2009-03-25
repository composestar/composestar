
class T15123rm3 {
    static class One {
	private void m() {
	    System.out.print(1);
	}
    }
    static class Two extends One {
	void m() {
	    System.out.print(2);
	}
    }
    public static void main(String[] args) {
	One o = new Two();
	o.m(); // invokes One.m(), even though this is an instance of Two
    }
}
    