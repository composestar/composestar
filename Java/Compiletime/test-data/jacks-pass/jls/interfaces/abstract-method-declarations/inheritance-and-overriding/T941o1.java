
class T941o1 {
    
        interface I1 {
            void m();
            void m(int i);
        }
        interface I2 extends I1 {
            void m();
        }
        void foo(I2 i) {
            i.m(1);
        }
    
}
