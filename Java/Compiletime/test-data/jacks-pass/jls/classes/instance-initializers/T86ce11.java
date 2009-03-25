
public class T86ce11 {
    T86ce11 (){}
    public static void main(String[] args) {
        
        try {
            new Object() {
                int m() throws ClassNotFoundException { return 1; }
                { m(); }
            };
        } catch (ClassNotFoundException e) {
        }
    
    }
}
