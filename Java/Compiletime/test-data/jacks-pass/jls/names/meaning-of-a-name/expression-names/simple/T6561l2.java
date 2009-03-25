
public class T6561l2 {
    T6561l2 (){}
    public static void main(String[] args) {
        
        final int i = 1;
        new Object() {
            void foo() {
                Object i;
                i = null; // refers to the local Object i, which shadows int i
            }
        };
    
    }
}
