
class T1525cotm3 {
    
        void foo(char c) throws Exception {}
        void foo(int i) {}
        void bar(boolean b) {
            foo(b ? '0' : -1);
            foo(b ? -1 : '0');
        }
    
}
