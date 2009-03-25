
class T8851q18 {
    T8851q18(Object o) {}
}
class T8851q18a {
    class Middle extends T8851q18 {
	Middle(int i) {
	    super(null);
	}
	Middle() {
	    // Here, the innermost instance of T8851q18a to enclose
	    // new Middle is T8851q18a.this
	    super(T8851q18a.this.new Middle(1).new Inner() {});
	}
	class Inner {}
    }
}
    