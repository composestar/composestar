
class T1525botm9 {
    
        void foo(byte b) throws Exception {}
        void foo(char c) throws Exception {}
        void foo(int i) {}
        void bar(boolean b, byte b1) {
            foo(b ? b1 : '0');
            foo(b ? '0' : b1);
        }
    
}
