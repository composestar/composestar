
class T652s1 {
    
        T652s1 t, u;
        static class t {
            static int u;
        }
        void foo() { // t refers to the field, not the class, thus t.u
            t.u = null; // is an Object, not an int
        }
    
}
