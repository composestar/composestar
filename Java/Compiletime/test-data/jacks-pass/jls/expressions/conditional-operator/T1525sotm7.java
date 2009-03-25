
class T1525sotm7 {
    
        void foo(short s) {}
        void foo(byte b) throws Exception {}
        void foo(int i) throws Exception {}
        void bar(boolean b, short s) {
            foo(b ? (byte) 0 : s);
            foo(true ? (byte) 0 : s);
            foo(false ? (byte) 0 : s);
        }
    
}
