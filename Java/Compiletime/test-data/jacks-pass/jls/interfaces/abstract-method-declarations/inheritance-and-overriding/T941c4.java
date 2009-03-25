
class T941c4 {
    
        class E1 extends Exception {}
        class E2 extends Exception {}
        interface I1 { void m() throws E1; }
        interface I2 { void m() throws E2; }
        interface I3 extends I1, I2 {}
    
}
