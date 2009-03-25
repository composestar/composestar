
public class T6551n3 {
    T6551n3 (){}
    public static void main(String[] args) {
        
        class C { // local
            static final int i = 1;
        }
        new Object() {
            class C { // member
                static final int i = 2;
            }
            void foo(int j) {
                switch (j) {
                    case 0:
                    case 1:
                    // C refers to the member class, since the local is
                    // shadowed, hence C.i is 2
                    case C.i:
                }
            }
        };
    
    }
}
