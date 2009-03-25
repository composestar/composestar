
class T652q6 {
    
        static class Super {
            static Super t, u;
        }
        static class Sub extends Super {
            static class t {
                static int u;
            }
            void foo() { // t refers to the inherited field, not the locally
                Sub.t.u = null; // declared class, thus Sub.t.u is an Object
            }
        }
    
}
