
class T94c3 {
    
        interface I1 { void m(); }
        interface I2 { void m(); }
        interface I3 extends I1, I2 {}
        void foo(I3 i) {
            i.m();
        }
    
}
