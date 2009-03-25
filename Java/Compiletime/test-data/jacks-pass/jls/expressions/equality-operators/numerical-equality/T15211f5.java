
class T15211f5 {
    T15211f5 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((Float.NaN != 0) ? 1 : 0):
            case ((!(Float.NaN == 0)) ? 2 : 0):
            case ((Float.NaN != -0f) ? 3 : 0):
            case ((!(Float.NaN == -0f)) ? 4 : 0):
        }
    }
}
