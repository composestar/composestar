
public class T6561l3 {
    T6561l3 (){}
    public static void main(String[] args) {
        
        final int i = 1;
        new Object() {
            Object i;
            void foo() {
                i = null; // the field Object i shadows the local int i
            }
        };
    
    }
}
