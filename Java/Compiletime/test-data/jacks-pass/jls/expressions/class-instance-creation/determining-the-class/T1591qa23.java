
class T1591qa23 {
    
	private class One {}
	class Two {
	    Two() {
		T1591qa23.this.new One() {};
	    }
	}
    
}
