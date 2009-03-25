
public class T1623lc8 {
    T1623lc8 (){}
    public static void main(String[] args) {
        
        final int i;
        i = 1;
        class Local {
            int j = i;
        }
        new Local();
    
    }
}
