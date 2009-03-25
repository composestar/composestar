
class T8111da20b extends p1.T8111da20a {
    abstract class D extends C1 {
	// this implements I.m, but not C.m, since that was not accessible
	// therefore, C.m remains unimplemented, and D must be abstract
	public void m() {}
    }
}
    