
class T8851e1 {
    
	static class Outer {
	    class Inner {}
	}
	static class ChildOfInner extends Outer.Inner {
	    ChildOfInner() {
		(new Outer()).super();
	    }
	}
    
}
