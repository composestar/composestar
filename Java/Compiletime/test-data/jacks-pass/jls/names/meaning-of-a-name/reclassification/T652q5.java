
class T652q5 {
    
        static T652q5 t, u;
        static class t {
            static int u;
        }
        void foo() { // t refers to the field, not the class, thus T652q5.t.u
            T652q5.t.u = null; // is an Object, not an int
        }
    
}
