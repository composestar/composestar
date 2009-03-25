
class T1585fe3 {
    int i;
    T1585fe3 t;
}
class Sub extends T1585fe3 {
    void foo() {
        (super.t).i = 1;
    }
}
    