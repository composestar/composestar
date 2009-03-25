
class T6551n23d extends p1.T6551n23c {
    void m(int i) {
	switch (i) {
	    case 1:
	    case C.i: // a.C.i is not accessible, so this is b.C.i == 2
	}
    }
}
    