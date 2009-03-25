
class T3102f14 {
    T3102f14 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((08.f == 8) ? 1 : 0):
            case ((09.f == 9) ? 2 : 0):
            case ((08e0f == 8) ? 3 : 0):
            case ((09e0f == 9) ? 4 : 0):
            case ((08f == 8) ? 5 : 0):
            case ((09f == 9) ? 6 : 0):
            case ((08.F == 8) ? 7 : 0):
            case ((09.F == 9) ? 8 : 0):
            case ((08e0F == 8) ? 9 : 0):
            case ((09e0F == 9) ? 10 : 0):
            case ((08F == 8) ? 11 : 0):
            case ((09F == 9) ? 12 : 0):
        }
    }
}
