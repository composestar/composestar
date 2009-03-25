
class T1528i5a {
    static final String s = "a";
}
class T1528i5b {
    void m() {
	class Local extends T1528i5a {
	    void m() {
		boolean b = s instanceof String;
	    }
	}
    }
}
    