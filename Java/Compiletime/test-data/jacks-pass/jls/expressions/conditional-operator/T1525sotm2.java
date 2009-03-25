
class T1525sotm2 {
    
        void foo(short s) {}
        void foo(int i) throws Exception {}
        void bar(boolean b) {
            foo(b ? (short) 0 : 0);
        }
    
}
