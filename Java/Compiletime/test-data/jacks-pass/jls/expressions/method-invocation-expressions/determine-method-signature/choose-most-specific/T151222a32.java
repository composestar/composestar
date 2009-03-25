
class T151222a32 {
    
        interface I {
            String toString();
        }
        abstract class A implements I {}
        class B extends A {
            { super.toString(); }
        }
    
}
