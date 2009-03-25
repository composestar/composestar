
class T1591qc21 {
    
	private class One {}
	class Two {
	    Two() {
		T1591qc21.this.new One();
	    }
	}
    
}
