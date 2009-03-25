
class T1525cotm4 {
    
        void foo(char c) throws Exception {}
        void foo(int i) {}
        void bar() {
            foo(true ? '0' : -1);
            foo(false ? '0' : -1);
            foo(true ? -1 : '0');
            foo(false ? -1 : '0');
        }
    
}
