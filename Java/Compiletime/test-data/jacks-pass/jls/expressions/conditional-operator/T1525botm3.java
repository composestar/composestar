
class T1525botm3 {
    
        void foo(byte b) throws Exception {}
        void foo(int i) {}
        void bar(boolean b) {
            foo(b ? (byte) 0 : 128);
            foo(b ? 128 : (byte) 0);
        }
    
}
