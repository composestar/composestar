
class T85i4 {
    
	class One {
	    class C {}
	}
	class Two extends One {
	    C c = new Two.C();
	}
    
}
