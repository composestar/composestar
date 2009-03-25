
class T151222a25a {
    static void foo(int i) {}
}
class T151222a25b extends T151222a25a {
    static void foo(long l) throws Exception {}
    { foo(0); }
}
    