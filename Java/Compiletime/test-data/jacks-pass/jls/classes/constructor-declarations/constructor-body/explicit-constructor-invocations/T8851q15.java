
class T8851q15 {
    
        T8851q15(Object o) {}
        private class Middle extends T8851q15 {
            Middle(int i) {
                super(null);
            }
            Middle() {
                // Here, the innermost instance of T8851q15 to enclose
		// new Middle is this
                super(new Middle(1).new Inner() {});
            }
            class Inner {}
        }
    
}
