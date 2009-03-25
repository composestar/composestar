
class T15181i1 {
    T15181i1 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case (("0" == "" + 0) ? 1 : 0):
            case (("0" == 0 + "") ? 2 : 0):
            case (("0,0" == 0 + "," + 0) ? 3 : 0):
        }
    }
}
