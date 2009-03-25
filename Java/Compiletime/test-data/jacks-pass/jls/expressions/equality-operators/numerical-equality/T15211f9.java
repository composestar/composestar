
class T15211f9 {
    T15211f9 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((Float.POSITIVE_INFINITY == Float.POSITIVE_INFINITY) ? 1 : 0):
            case ((Float.POSITIVE_INFINITY != Float.NEGATIVE_INFINITY) ? 2 : 0):
            case ((Float.NEGATIVE_INFINITY == Float.NEGATIVE_INFINITY) ? 3 : 0):
            case ((Float.POSITIVE_INFINITY != 0) ? 4 : 0):
            case ((Float.NEGATIVE_INFINITY != -0f) ? 5 : 0):
        }
    }
}
