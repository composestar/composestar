
package p2;
import p1.*;
public class T15123rm1_2 extends T15123rm1_1 {
    protected void call() {
        foo(); // must be virtual call
        System.out.print(' ');
        new Object() {
            { foo(); } // must be virtual call of enclosing instance
        };
    }
}
    