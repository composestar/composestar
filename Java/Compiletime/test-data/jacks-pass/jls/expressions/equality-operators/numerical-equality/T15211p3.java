
class T15211p3 {
    T15211p3 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((.1f != .1) ? 1 : 0):
        }
    }
}
