
class T1525cotm8 {
    
        void foo(char c) throws Exception {}
        void foo(int i) {}
        void bar(char c, int i) {
            foo(true ? c : i);
            foo(false ? c : i);
            foo(true ? i : c);
            foo(false ? i : c);
        }
    
}
