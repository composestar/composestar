
import p1.T6551i2a;
import p2.*;
class T6551i2b {
    void foo(int j) {
        switch (j) {
            case 0:
            case 1:
            // T6551i2a refers to the specific p1 import, not the on-demand p2,
            // so T6551i2a.i resolves to 2
            case T6551i2a.i:
        }
    }
}
    