
class T1525cotm1 {
    
        void foo(char c) {}
        void foo(int i) throws Exception {}
        void bar(boolean b) {
            foo(b ? '0' : 0);
            foo(b ? 0 : '0');
        }
    
}
