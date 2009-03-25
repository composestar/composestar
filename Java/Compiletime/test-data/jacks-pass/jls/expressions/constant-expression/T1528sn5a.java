
class T1528sn5a {
    static final String s = "a";
}
class T1528sn5b {
    void m() {
	class Local extends T1528sn5a {
	    void m() {
		s.toString();
		s.valueOf(1);
	    }
	}
    }
}
    