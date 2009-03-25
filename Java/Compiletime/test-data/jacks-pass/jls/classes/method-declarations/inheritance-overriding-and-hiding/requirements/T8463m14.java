
class T8463m14 {
    
	abstract static class One {
	    strictfp void strict() {}
	    abstract void abs();
	    native void nat();
	    synchronized void synch() {}
	    static strictfp void sstrict() {}
	    static native void snat();
	    static synchronized void ssynch() {}
	}
	abstract static class Two extends One {
	    abstract void strict();
	    native void abs();
	    synchronized void nat() {}
	    strictfp void synch() {}
	    static native void sstrict();
	    static synchronized void snat() {}
	    static strictfp void ssynch() {}
	}
    
}
