
class T1525sotm9 {
    
        void foo(short s) {}
        void foo(int i) throws Exception {}
        void bar(boolean b, short s) {
            foo(b ? s : 0);
            foo(b ? 0 : s);
        }
    
}
