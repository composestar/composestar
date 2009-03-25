
class T1521p1 {
    T1521p1 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((1 < 2 == 2 < 3) ? 1 : 0):
            case (((1 < 2) == (2 < 3)) ? 2 : 0):
        }
    }
}
