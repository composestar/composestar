
class T15173d1 {
    T15173d1 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((Double.NaN % Double.NaN != Double.NaN % Double.NaN) ? 1 : 0):
        }
    }
}
