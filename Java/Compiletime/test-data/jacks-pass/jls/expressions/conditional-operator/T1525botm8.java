
class T1525botm8 {
    
        void foo(byte b) throws Exception {}
        void foo(int i) {}
        void bar(byte b, int i) {
            foo(true ? b : i);
            foo(false ? b : i);
            foo(true ? i : b);
            foo(false ? i : b);
        }
    
}
