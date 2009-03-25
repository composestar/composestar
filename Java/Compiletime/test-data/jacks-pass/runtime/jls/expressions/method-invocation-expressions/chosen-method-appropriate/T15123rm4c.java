
class T15123rm4b extends p1.T15123rm4a {
    public void foo() {
	m();
	super.m();
	new Object() {
	    {
		m();
		T15123rm4b.super.m();
	    }
	};
    }
    protected void m() {
	System.out.print("b ");
    }
}
class T15123rm4c extends T15123rm4b {
    public void m() {
	System.out.print("c ");
    }
    public static void main(String[] args) {
	new T15123rm4d();
    }
}
class T15123rm4d extends T15123rm4c {
    public void foo() {}
    T15123rm4d() {
	new Object() {
	    {
		T15123rm4d.super.foo();
	    }
	};
    }
}	
    