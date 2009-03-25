
class T1585fe6 {
    int i;
    T1585fe6 t;
}
class Sub extends T1585fe6 {
    class Inner {
        void foo() {
            (Sub.super.t).i = 1;
        }
    }
}
    