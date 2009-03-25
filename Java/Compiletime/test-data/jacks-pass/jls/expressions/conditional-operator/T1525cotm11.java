
class T1525cotm11 {
    
        void foo(short s) throws Exception {}
        void foo(char c) throws Exception {}
        void foo(int i) {}
        void bar(boolean b, char c, short s) {
            foo(b ? c : s);
            foo(b ? s : c);
        }
    
}
