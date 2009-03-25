
class T8111da16b extends p1.T8111da16a {
    abstract class D extends C implements I {
	// since C.m is not accessible, this only implements I.m, and hence can
	// throw. However, since C.m remains unimplemented, a concrete subclass
	// will have to override D.m and lose the throws clause
	public void m() throws Exception {}
    }
}
    