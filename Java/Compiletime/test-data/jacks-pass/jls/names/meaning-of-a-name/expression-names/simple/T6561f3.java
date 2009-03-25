
class T6561f3 {
    
        int i;
        class Inner {
            Object i;
            void foo() {
                i = null; // refers to Object Inner.i, not int T6561f3.i
            }
        }
    
}
