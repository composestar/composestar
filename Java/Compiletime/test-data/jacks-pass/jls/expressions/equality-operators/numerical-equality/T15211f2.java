
class T15211f2 {
    T15211f2 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((0f == -0f) ? 1 : 0):
            case ((!(0f != -0f)) ? 2 : 0):
        }
    }
}
