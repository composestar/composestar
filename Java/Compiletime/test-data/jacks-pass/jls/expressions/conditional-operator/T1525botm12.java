
class T1525botm12 {
    
        void foo(byte b) throws Exception {}
        void foo(char c) throws Exception {}
        void foo(int i) {}
        void bar(byte b, char c) {
            foo(true ? b : c);
            foo(false ? b : c);
            foo(true ? c : b);
            foo(false ? c : b);
        }
    
}
