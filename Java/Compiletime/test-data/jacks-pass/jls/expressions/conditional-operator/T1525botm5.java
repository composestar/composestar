
class T1525botm5 {
    
        void foo(byte b) {}
        void foo(int i) throws Exception {}
        void bar(boolean b, byte b1) {
            foo(b ? b1 : 0);
            foo(b ? 0 : b1);
        }
    
}
