
class T15211f1 {
    T15211f1 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((0. == -0.) ? 1 : 0):
            case ((!(0. != -0.)) ? 2 : 0):
        }
    }
}
