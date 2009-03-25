
public class T6551n5 {
    T6551n5 (){}
    public static void main(String[] args) {
        
        class C {
            static final int i = 1;
        }
        new Object() {
            void foo(int j) {
                class C {
                    static final int i = 2;
                }
                switch (j) {
                    case 0:
                    case 1:
                    // C refers to the innermost local class, since it shadows
                    // the other version, hence C.i is 2
                    case C.i:
                }
            }
        };
    
    }
}
