
class T151222a28a {
    void foo(int i) throws Exception {}
}
class T151222a28b extends T151222a28a {
    void foo(long l) {}
    { foo(0); }
}
    