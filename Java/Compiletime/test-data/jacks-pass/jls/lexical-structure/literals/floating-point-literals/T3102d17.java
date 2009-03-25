
class T3102d17 {
    T3102d17 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((0e+0d == 0e-0d) ? 1 : 0):
            case ((0E1 == 0) ? 2 : 0):
            case ((0d == 0D) ? 3 : 0):
        }
    }
}
