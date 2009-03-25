
class T852nsmu7 {
    
        void foo() {}
        static class T852nsmu7_Test {
            void bar(T852nsmu7 t) {
                t.foo(); // foo is qualified, doesn't count as usage
            }
        }
    
}
