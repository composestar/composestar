
class T15172d12 {
    T15172d12 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((1e-160 / 1e160 == 1e-320) ? 1 : 0):
            case ((1e-160 / -1e160 == -1e-320) ? 2 : 0):
        }
    }
}
