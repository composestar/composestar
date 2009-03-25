
class T8851q14 {
    
        T8851q14(Object o) {}
        class Middle extends T8851q14 {
            Middle(int i) {
                super(null);
            }
            Middle() {
                // Here, new Middle is a member of Middle
                super(T8851q14.this.new Middle(1).new Inner() {});
            }
            class Inner {}
        }
    
}
