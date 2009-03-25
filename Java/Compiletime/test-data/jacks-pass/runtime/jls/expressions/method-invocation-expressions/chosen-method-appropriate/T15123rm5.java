
class T15123rm5 {
    interface I {
	void m();
    }
    static abstract class One implements I {}
    static class Two extends One {
	public void m() {
	    System.out.print(1);
	}
    }
    public static void main(String[] args) {
	One o = new Two();
	I i = o;
	o.m(); // virtual
	System.out.print(' ');
	i.m(); // interface
    }
}
    