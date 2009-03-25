
class T8461o2 {
    
	static class One {
	    static void m() {}
	}
	static class Two extends One {
	    int m(int i) throws Exception { return 1; }
	}
	static class Three extends Two {
	    {
		m();
		this.m();
		try {
		    m(1);
		    this.m(1);
		} catch (Exception e) {
		}
	    }
	}
    
}
