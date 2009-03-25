
class T151222a17a {
    static void foo(int i) {}
}
class T151222a17b extends T151222a17a {
    static void foo(long l) {}
    { foo(0); }
}
    