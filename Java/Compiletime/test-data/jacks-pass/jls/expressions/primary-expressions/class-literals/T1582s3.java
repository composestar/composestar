
public class T1582s3 {
    T1582s3 (){}
    public static void main(String[] args) {
        
        class Local {
            Local(Object o) {}
            void m() {
                Class c = Boolean.class;
            }
        }
        new Local(new Object() {
            Class c = Byte.class;
        });
    
    }
}
