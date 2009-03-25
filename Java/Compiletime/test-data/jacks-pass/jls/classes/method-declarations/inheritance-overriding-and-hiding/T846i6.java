
class T846i6 {
    
	class One {
	    int m() { return 1; }
	}
	class Two extends One {
	    {
		m();
		this.m();
	    }
	}
    
}
