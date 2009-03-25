
class T15181f1 {
    T15181f1 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case (("" + 1e-44f == "9.8E-45") ? 1 : 0):
            case ((1e-44f + "" == "9.8E-45") ? 2 : 0):
        }
    }
}
