
class T165r5 {
    public static void main(String[] args) {
	final int i;
	i = 1;
	class A {
	    class B {
		class C {
		    {
			new A() {
			    { System.out.print(i); }
			};
		    }
		}
	    }
	}
	new A(){}.new B(){}.new C(){};
    }
}
    