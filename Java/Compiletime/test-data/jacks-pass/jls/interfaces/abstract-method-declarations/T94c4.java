
class T94c4 {
    
        interface I1 { void m(); }
        interface I2 extends I1 {}
        interface I3 extends I1, I2 {}
        void bar(I3 i) {
            i.m();
        }
    
}
