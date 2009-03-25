
class T15181c2 {
    T15181c2 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case (("a" == "" + 'a') ? 1 : 0):
            case (("a" == 'a' + "") ? 2 : 0):
            case (("a,a" == 'a' + "," + 'a') ? 3 : 0):
        }
    }
}
