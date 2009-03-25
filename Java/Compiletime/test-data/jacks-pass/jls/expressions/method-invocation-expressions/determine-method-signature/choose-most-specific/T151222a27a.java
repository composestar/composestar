
class T151222a27a {
    void foo(int i) {}
}
class T151222a27b extends T151222a27a {
    void foo(long l) throws Exception {}
    { foo(0); }
}
    