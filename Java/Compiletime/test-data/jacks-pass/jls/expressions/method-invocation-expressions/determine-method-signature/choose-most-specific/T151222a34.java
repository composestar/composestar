
class T151222a34 {
    
        interface I {}
        abstract class A implements I {}
        class B extends A {
            { super.toString(); }
        }
    
}
