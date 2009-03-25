
package p1;
class T6531s2 {
    static int i;
    void foo() {
        p1.T6531s2.i++; // p1 was reclassified from ambiguous
    }
}
