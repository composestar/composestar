
class T1525cotm6 {
    
        void foo(char c) {}
        void foo(int i) throws Exception {}
        void bar(char c) {
            foo(true ? c : 0);
            foo(false ? c : 0);
            foo(true ? 0 : c);
            foo(false ? 0 : c);
        }
    
}
