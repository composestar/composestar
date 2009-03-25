
package p3;
import p2.*;
public class T15123rm1_3 extends T15123rm1_2 {
    public static void main(String[] args) {
        new T15123rm1_3().call();
    }
    protected void foo() { // will be called virtually
        System.out.print("3");
    }
}
    