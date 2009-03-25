
class T1525cotm7 {
    
        void foo(char c) throws Exception {}
        void foo(int i) {}
        void bar(boolean b, char c, int i) {
            foo(b ? c : i);
            foo(b ? i : c);
        }
    
}
