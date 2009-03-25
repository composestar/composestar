
class T1525sotm10 {
    
        void foo(short s) {}
        void foo(int i) throws Exception {}
        void bar(short s) {
            foo(true ? s : 0);
            foo(false ? s : 0);
            foo(true ? 0 : s);
            foo(false ? 0 : s);
        }
    
}
