
class T6551n7 {
    
        interface C {}
        static class Super {
            class C {}
        }
        void foo() {
            new Super() {
                // C refers to the inherited class Super.C, not the shadowed
                // interface T6551n7.C
                class Sub extends C {}
            };
        }
    
}
