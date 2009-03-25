
class T15211f4 {
    T15211f4 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((Double.NaN != 0) ? 1 : 0):
            case ((!(Double.NaN == 0)) ? 2 : 0):
            case ((Double.NaN != -0.) ? 3 : 0):
            case ((!(Double.NaN == -0.)) ? 4 : 0):
        }
    }
}
