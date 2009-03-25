
class T15181l1 {
    T15181l1 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case (("0" == "" + 0L) ? 1 : 0):
            case (("0" == 0L + "") ? 2 : 0):
            case (("0,0" == 0L + "," + 0L) ? 3 : 0):
        }
    }
}
