
package p1;
public class T8111da24a {
    abstract class C {
	abstract void m();
    }
    interface I {
	void m() throws Exception;
    }
    protected abstract class C1 extends C implements I {}
}
class T8111da24c extends p2.T8111da24b {
    class E extends D {
	// This overrides and implements C.m (even though B.m was not inherited)
	// so it cannot throw
	public void m() {}
    }
}
    