
class T3107v17 {
    void foo(int i) {
	switch (i) {
	    case 0:
	    case (("\0000" == '\u0000' + "0") ? 1 : 0):
	}
    }
}
    