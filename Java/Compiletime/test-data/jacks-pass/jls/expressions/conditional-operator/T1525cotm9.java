
class T1525cotm9 {
    
        void foo(short s) throws Exception {}
        void foo(char c) throws Exception {}
        void foo(int i) {}
        void bar(boolean b, char c) {
            foo(b ? c : (short) 0);
            foo(b ? (short) 0 : c);
        }
    
}
