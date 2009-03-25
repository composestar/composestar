
class T6561l4 {
    
        class Super {
            Object i;
        }
        void foo(final int i) {
            new Super() {
                void foo() {
                    // the inherited field Object i shadows the parameter int i
                    i = null;
                }
            };
        }
    
}
