
class T151241rsuper2 {
    abstract class Super {
	private void a() { System.out.print(1 + e()); }
	void b() { System.out.print(2 + e()); }
	protected void c() { System.out.print(3 + e()); }
	public void d() { System.out.print(4 + e()); }
	abstract String e();
    }
    class Sub extends Super {
	private void a() { System.out.println(5); }
	void b() { System.out.println(6); }
	protected void c() { System.out.println(7); }
	public void d() { System.out.println(8); }
	String e() { return "s "; }
	class Inner extends Super {
	    private void a() { System.out.println(9); }
	    void b() { System.out.println(10); }
	    protected void c() { System.out.println(11); }
	    public void d() { System.out.println(12); }
	    String e() { return "i "; }
	    void m() {
		super.a();
		super.b();
		super.c();
		super.d();
		Sub.super.a();
		Sub.super.b();
		Sub.super.c();
		Sub.super.d();
	    }
	}
    }
    public static void main(String[] args) {
	new T151241rsuper2().new Sub().new Inner().m();
    }
}
    