
class T8111da22b extends p1.T8111da22a {
    abstract class D extends C1 {
	// since C.m is not accessible, this only implements I.m, and hence can
	// throw. However, since C.m remains unimplemented, a concrete subclass
	// will have to override D.m and lose the throws clause
	public void m() throws Exception {}
    }
}
    