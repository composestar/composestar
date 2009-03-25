
public class T1623lc6 {
    T1623lc6 (){}
    public static void main(String[] args) {
        
        final int i = 1;
        class Local {
            int j = i;
        }
        new Local();
    
    }
}
