
class T15211f7 {
    T15211f7 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((Float.NaN != Float.POSITIVE_INFINITY) ? 1 : 0):
            case ((!(Float.NaN == Float.POSITIVE_INFINITY)) ? 2 : 0):
            case ((Float.NaN != Float.NEGATIVE_INFINITY) ? 3 : 0):
            case ((!(Float.NaN == Float.NEGATIVE_INFINITY)) ? 4 : 0):
        }
    }
}
