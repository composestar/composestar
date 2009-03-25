
class T1525sotm6 {
    
        void foo(short s) {}
        void foo(byte b) throws Exception {}
        void foo(int i) throws Exception {}
        void bar(boolean b, byte b1) {
            foo(b ? b1 : (short) 0);
            foo(true ? b1 : (short) 0);
            foo(false ? b1 : (short) 0);
        }
    
}
