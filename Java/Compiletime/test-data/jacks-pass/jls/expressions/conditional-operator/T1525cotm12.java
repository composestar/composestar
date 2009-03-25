
class T1525cotm12 {
    
        void foo(short s) throws Exception {}
        void foo(char c) throws Exception {}
        void foo(int i) {}
        void bar(char c, short s) {
            foo(true ? c : s);
            foo(false ? c : s);
            foo(true ? s : c);
            foo(false ? s : c);
        }
    
}
