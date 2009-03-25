
class T151222a26a {
    static void foo(int i) throws Exception {}
}
class T151222a26b extends T151222a26a {
    static void foo(long l) {}
    { foo(0); }
}
    