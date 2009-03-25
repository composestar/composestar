
class T6561f4 {
    
        int i;
        class Super {
            Object i;
        }
        class Sub extends Super {
            void foo() {
                // refers to inherited Object Super.i, not int T6561f3.i
                i = null;
            }
        }
    
}
