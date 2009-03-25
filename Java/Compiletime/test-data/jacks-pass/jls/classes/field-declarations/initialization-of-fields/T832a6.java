
public class T832a6 {
    T832a6 (){}
    public static void main(String[] args) {
        
        try {
            new Object() {
                int m() throws ClassNotFoundException { return 1; }
                int i = m();
            };
        } catch (ClassNotFoundException e) {
        }
    
    }
}
