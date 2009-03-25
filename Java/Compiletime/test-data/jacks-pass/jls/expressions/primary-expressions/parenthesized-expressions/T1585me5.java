
class T1585me5 {
    int bar() { return 1; }
}
class Sub extends T1585me5 {
    int i = (super.bar());
}
    