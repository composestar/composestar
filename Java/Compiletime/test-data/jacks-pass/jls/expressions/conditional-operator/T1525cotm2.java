
class T1525cotm2 {
    
        void foo(char c) throws Exception {}
        void foo(int i) {}
        void bar(boolean b, int i) {
            foo(b ? '0' : i);
            foo(b ? i : '0');
        }
    
}
