
class T15171f6 {
    T15171f6 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((Float.NEGATIVE_INFINITY * -1f == Float.POSITIVE_INFINITY) ? 1 : 0):
            case ((Float.POSITIVE_INFINITY * -1f == Float.NEGATIVE_INFINITY) ? 2 : 0):
        }
    }
}
