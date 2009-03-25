
class T1525botm2 {
    
        void foo(byte b) throws Exception {}
        void foo(int i) {}
        void bar(boolean b, int i) {
            foo(b ? (byte) 0 : i);
            foo(b ? i : (byte) 0);
        }
    
}
