
class T6551n4 {
    
        static class Super {
            interface C {} // member
        }
        void foo() {
            class C {} // local
            new Super() {
                // C refers to the inherited member class, since the
                // local is shadowed, hence C is an interface
                class Sub implements C {}
            };
        }
    
}
