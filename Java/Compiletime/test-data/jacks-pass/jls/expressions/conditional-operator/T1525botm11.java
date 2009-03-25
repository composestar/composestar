
class T1525botm11 {
    
        void foo(byte b) throws Exception {}
        void foo(char c) throws Exception {}
        void foo(int i) {}
        void bar(boolean b, byte b1, char c) {
            foo(b ? b1 : c);
            foo(b ? c : b1);
        }
    
}
