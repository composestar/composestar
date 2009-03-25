
class T15211f6 {
    T15211f6 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((Double.NaN != Double.POSITIVE_INFINITY) ? 1 : 0):
            case ((!(Double.NaN == Double.POSITIVE_INFINITY)) ? 2 : 0):
            case ((Double.NaN != Double.NEGATIVE_INFINITY) ? 3 : 0):
            case ((!(Double.NaN == Double.NEGATIVE_INFINITY)) ? 4 : 0):
        }
    }
}
