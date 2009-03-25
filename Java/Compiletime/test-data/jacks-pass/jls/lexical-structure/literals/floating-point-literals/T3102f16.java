
class T3102f16 {
    T3102f16 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((0e+0f == 0e-0f) ? 1 : 0):
            case ((0E1f == 0) ? 2 : 0):
            case ((0f == 0F) ? 3 : 0):
        }
    }
}
