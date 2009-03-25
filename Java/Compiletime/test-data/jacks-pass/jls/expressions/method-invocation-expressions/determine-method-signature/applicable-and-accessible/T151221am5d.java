
class T151221am5d {
    // a concrete subclass of c will necessarily have an implementation of m
    // that does not throw, in order to implement a.m; but since only b.m is
    // accessible here, the catch clause is reachable
    void m(p1.T151221am5c t) {
	try {
	    t.m();
	} catch (Exception e) {
	}
    }
}
    