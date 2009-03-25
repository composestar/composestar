
class T83h4 {
    
	class One {
	    final int fin = 1;
	    transient int trans;
	    volatile int vol;
	    int plain;
	}
	class Two extends One {
	    int fin;
	    int trans;
	    int vol;
	    int plain;
	}
	class Three extends One {
	    final int fin = 1;
	    final int trans = 1;
	    final int vol = 1;
	    final int plain = 1;
	}
	class Four extends One {
	    transient int fin;
	    transient int trans;
	    transient int vol;
	    transient int plain;
	}
	class Five extends One {
	    volatile int fin;
	    volatile int trans;
	    volatile int vol;
	    volatile int plain;
	}
    
}
