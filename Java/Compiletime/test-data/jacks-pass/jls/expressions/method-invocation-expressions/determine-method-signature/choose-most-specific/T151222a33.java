
class T151222a33 {
    
        interface I {}
        class A implements I {}
        class B extends A {
            { super.toString(); }
        }
    
}
