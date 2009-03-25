
class T1525botm7 {
    
        void foo(byte b) throws Exception {}
        void foo(int i) {}
        void bar(boolean b, byte b1, int i) {
            foo(b ? b1 : i);
            foo(b ? i : b1);
        }
    
}
