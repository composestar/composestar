
class T15211f3 {
    T15211f3 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((Double.NaN != Double.NaN) ? 1 : 0):
            case ((!(Double.NaN == Double.NaN)) ? 2 : 0):
            case ((Float.NaN != Float.NaN) ? 3 : 0):
            case ((!(Float.NaN == Float.NaN)) ? 4 : 0):
        }
    }
}
