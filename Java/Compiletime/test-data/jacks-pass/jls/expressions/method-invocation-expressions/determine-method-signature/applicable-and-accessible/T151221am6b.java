
class T151221am6b extends p1.T151221am6a {
    interface B {
	void m() throws E2;
    }
    static abstract class C extends A implements B {
	// c inherits two versions of m
    }
    static abstract class D extends C {
	// likewise, d inherits two versions of m, but protected accessibility
	// rules state that access to a.m is only possible when the qualifier
	// is d or below. When both methods are accessible, the method cannot
	// throw either exception; but alone, b.m may throw.
	{
	    m(); // merged throws clause
	    try {
		((C) this).m(); // only b.m accessible
	    } catch (E2 e) {
	    }
	}
    }
}
    