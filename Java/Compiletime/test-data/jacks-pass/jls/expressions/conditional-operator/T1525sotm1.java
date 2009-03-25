
class T1525sotm1 {
    
        void foo(short s) {}
        void foo(byte b) throws Exception {}
        void foo(int i) throws Exception {}
        void bar(boolean b) {
            foo(b ? (byte) 0 : (short) 0);
            foo(true ? (byte) 0 : (short) 0);
            foo(false ? (byte) 0 : (short) 0);
        }
    
}
