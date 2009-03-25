
class T652s2 {
    
        static class Super {
            Super t, u;
        }
        static class Sub extends Super {
            static class t {
                static int u;
            }
            void foo() { // t refers to the inherited field, not the locally
                t.u = null; // declared class, thus t.u is an Object
            }
        }
    
}
