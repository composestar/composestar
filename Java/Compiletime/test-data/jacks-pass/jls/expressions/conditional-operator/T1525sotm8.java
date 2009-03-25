
class T1525sotm8 {
    
        void foo(short s) {}
        void foo(byte b) throws Exception {}
        void foo(int i) throws Exception {}
        void bar(boolean b, byte b1, short s) {
            foo(b ? b1 : s);
            foo(true ? b1 : s);
            foo(false ? b1 : s);
        }
    
}
