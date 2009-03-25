
class T1585p1 {
    T1585p1 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((1+2*3 == 7) ? 1 : 0):
            case (((1+2)*3 == 9) ? 2 : 0):
            case ((1+(2*3) == 7) ? 3 : 0):
        }
    }
}
