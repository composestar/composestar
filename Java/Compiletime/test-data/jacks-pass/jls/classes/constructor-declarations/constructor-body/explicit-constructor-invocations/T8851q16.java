
class T8851q16 {
    
        T8851q16(Object o) {}
        private class Middle extends T8851q16 {
            Middle(int i) {
                super(null);
            }
            Middle() {
                // Here, new Middle is a member of T8851q16, since it was
		// private and not inherited as a member of Middle
                super(T8851q16.this.new Middle(1).new Inner() {});
            }
            class Inner {}
        }
    
}
