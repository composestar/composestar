
import p2.T6551n15b;
class T6551n15c extends p1.T6551n15a {
    void foo() {
        // resolves to the inherited interface, not the imported class
        new T6551n15b() {};
    }
}
    