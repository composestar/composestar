
class T92i16 {
    
        interface I { Object clone() throws java.io.IOException; }
        abstract class C implements I {
            public abstract Object clone();
        }
        void m(C c) {
            c.clone(); // cannot throw a checked exception
        }
    
}
