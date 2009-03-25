
class T8851q17 {
    T8851q17(Object o) {}
}
class T8851q17a {
    class Middle extends T8851q17 {
	Middle(int i) {
	    super(null);
	}
	Middle() {
	    // Here, the innermost instance of T8851q17a to enclose
	    // new Middle is T8851q17a.this
	    super(new Middle(1).new Inner() {});
	}
	class Inner {}
    }
}
    