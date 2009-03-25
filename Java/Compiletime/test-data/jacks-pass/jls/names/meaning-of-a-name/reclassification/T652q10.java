
class T652q10 {
    
        T652q10 u;
        int i;
        void foo(T652q10 t) {
            t.u.i++; // u is a field of type T652q10, so t.u is an expression
        }
    
}
