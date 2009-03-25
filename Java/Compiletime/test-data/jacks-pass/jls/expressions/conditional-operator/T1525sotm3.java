
class T1525sotm3 {
    
        void foo(short s) throws Exception {}
        void foo(int i) {}
        void bar(boolean b, int i) {
            foo(b ? (short) 0 : i);
        }
    
}
