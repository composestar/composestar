
class T1585me8 {
    int bar() { return 1; }
}
class Sub extends T1585me8 {
    class Inner {
        Inner() {
            int i = (Sub.super.bar());
        }
    }
}
    