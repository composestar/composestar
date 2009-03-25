
package p1;
public class T8111da18a {
    public abstract class C {
	abstract void m();
    }
    public interface I {
	void m() throws Exception;
    }
}
class T8111da18c extends p2.T8111da18b {
    class E extends D {
	// This overrides and implements C.m (even though B.m was not inherited)
	// so it cannot throw
	public void m() {}
    }
}
    