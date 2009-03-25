
public class T86ce5 {
    T86ce5 (){}
    public static void main(String[] args) {
        
        try {
            new Object() {
                { if (true) throw new ClassNotFoundException(); }
            };
        } catch (ClassNotFoundException e) {
        }
    
    }
}
