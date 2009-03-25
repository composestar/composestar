
class T1525botm4 {
    
        void foo(byte b) throws Exception {}
        void foo(int i) {}
        void bar() {
            foo(true ? (byte) 0 : 128);
            foo(false ? (byte) 0 : 128);
            foo(true ? 128 : (byte) 0);
            foo(false ? 128 : (byte) 0);
        }
    
}
