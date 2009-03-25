
class T8463m1 {
    
	static class One {
	    public void pub() {}
	    protected void pro() {}
	    void pack() {}
	    public static void spub() {}
	    protected static void spro() {}
	    static void spack() {}
	}
	static class Two extends One {
	    public void pub() {}
	    public void pro() {}
	    public void pack() {}
	    public static void spub() {}
	    public static void spro() {}
	    public static void spack() {}
	}
	static class Three extends One {
	    protected static void spro() {}
	    protected static void spack() {}
	}
	static class Four extends One {
	    static void spack() {}
	}
    
}
