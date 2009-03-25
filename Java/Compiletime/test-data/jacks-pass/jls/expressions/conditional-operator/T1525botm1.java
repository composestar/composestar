
class T1525botm1 {
    
        void foo(byte b) {}
        void foo(int i) throws Exception {}
        void bar(boolean b) {
            foo(b ? (byte) 0 : 0);
            foo(b ? 0 : (byte) 0);
        }
    
}
