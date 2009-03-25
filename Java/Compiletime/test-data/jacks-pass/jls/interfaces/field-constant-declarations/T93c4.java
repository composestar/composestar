
class T93c4 {
    
        interface I1 { int i = 1; }
        interface I2 extends I1 {}
        interface I3 extends I1, I2 { int j = i; }
    
}
