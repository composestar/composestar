
class T1525cotm10 {
    
        void foo(short s) throws Exception {}
        void foo(char c) throws Exception {}
        void foo(int i) {}
        void bar(char c) {
            foo(true ? c : (short) 0);
            foo(false ? c : (short) 0);
            foo(true ? (short) 0 : c);
            foo(false ? (short) 0 : c);
        }
    
}
