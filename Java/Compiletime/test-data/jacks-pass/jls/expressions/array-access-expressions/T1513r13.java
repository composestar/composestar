
class T1513r13 {
    
	static int[] i;
	void m() { 
	    i[0]++;
	    this.i[0]++;
	    T1513r13.i[0]++;
	}
    
}
