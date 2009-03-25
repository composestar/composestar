
class T8111da14b extends p1.T8111da14a {
    abstract class D extends C implements I {
	// this implements I.m, but not C.m, since that was not accessible
	// therefore, C.m remains unimplemented, and D must be abstract
	public void m() {}
    }
}
    