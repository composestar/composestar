
import java.lang.reflect.Field;
class T131c4 {
    static {
        try {
            Field f = T131c4.class.getField("i");
            System.out.print(f.getInt(null));
        } catch (Exception e) {
        }
    }
    {
        try {
            Field f = T131c4.class.getField("j");
            System.out.print(f.getInt(this));
        } catch (Exception e) {
        }
    }
    public static final int i = 1;
    public final int j = 2;
    static {
        try {
            Field f = T131c4.class.getField("i");
            System.out.print(f.getInt(null));
        } catch (Exception e) {
        }
    }
    {
        try {
            Field f = T131c4.class.getField("j");
            System.out.print(f.getInt(this));
        } catch (Exception e) {
        }
    }
    public static void main(String[] args) {
	new T131c4();
    }
}
    