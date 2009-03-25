
class T1525cotm5 {
    
        void foo(char c) {}
        void foo(int i) throws Exception {}
        void bar(boolean b, char c) {
            foo(b ? c : 0);
            foo(b ? 0 : c);
        }
    
}
