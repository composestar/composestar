
class T1528s6 {
    T1528s6 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case (("" + 5 * 3 == "15") ? 1 : 0):
            case (("" + 5 / 3 == "1") ? 2 : 0):
            case (("" + 5 % 3 == "2") ? 3 : 0):
        }
    }
}
