
class T15173d14 {
    T15173d14 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((5.0 % 3.0 == 2.0) ? 1 : 0):
            case ((5.0 % -3.0 == 2.0) ? 2 : 0):
            case ((-5.0 % 3.0 == -2.0) ? 3 : 0):
            case ((-5.0 % -3.0 == -2.0) ? 4 : 0):
        }
    }
}
