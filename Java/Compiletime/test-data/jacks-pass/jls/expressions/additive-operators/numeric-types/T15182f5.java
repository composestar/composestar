
class T15182f5 {
    T15182f5 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((Float.POSITIVE_INFINITY + -0.0f == Float.POSITIVE_INFINITY) ? 1 : 0):
        }
    }
}
