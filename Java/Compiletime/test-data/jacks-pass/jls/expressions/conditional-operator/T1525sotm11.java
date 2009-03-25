
class T1525sotm11 {
    
        void foo(short s) throws Exception {}
        void foo(int i) {}
        void bar(boolean b, short s, int i) {
            foo(b ? s : i);
            foo(b ? i : s);
        }
    
}
