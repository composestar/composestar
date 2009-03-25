
import java.lang.reflect.Modifier;
class T1431r1 {
    public static void main(String[] args) {
        class Local {}
        Local l = new Local();
        System.out.print(Modifier.isStatic(l.getClass().getModifiers()));
        System.out.print(' ');
        new T1431r1().foo();
    }
    void foo() {
        class Local1 {}
        Local1 l = new Local1();
        System.out.print(Modifier.isStatic(l.getClass().getModifiers()));
    }        
}
    