
abstract class T151221am3d extends p1.T151221am3c {
    // a concrete subclass of c will necessarily have an implementation of m
    // that does not throw, in order to implement a.m; but only b.m was
    // inherited here because of accessibility. Since b.m throws, the catch
    // clause is reachable
    {
	try {
	    m();
	} catch (Exception e) {
	}
    }
}
    