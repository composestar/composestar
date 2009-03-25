
class T8851q12 {
    
        class Super {
            Middle m;
        }
        class Middle extends Super {
            class A {}
            class B extends A {
                B() {
                    Middle.super.m.super();
                }
            }
        }
    
}
