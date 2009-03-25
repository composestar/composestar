
class T1525botm6 {
    
        void foo(byte b) {}
        void foo(int i) throws Exception {}
        void bar(byte b) {
            foo(true ? b : 0);
            foo(false ? b : 0);
            foo(true ? 0 : b);
            foo(false ? 0 : b);
        }
    
}
