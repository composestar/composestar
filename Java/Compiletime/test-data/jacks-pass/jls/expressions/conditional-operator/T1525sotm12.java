
class T1525sotm12 {
    
        void foo(short s) throws Exception {}
        void foo(int i) {}
        void bar(short s, int i) {
            foo(true ? s : i);
            foo(false ? s : i);
            foo(true ? i : s);
            foo(false ? i : s);
        }
    
}
