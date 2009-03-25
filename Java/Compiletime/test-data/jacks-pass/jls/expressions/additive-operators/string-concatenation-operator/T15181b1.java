
class T15181b1 {
    T15181b1 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case (("" + true == "true") ? 1 : 0):
            case (("" + false == "false") ? 2 : 0):
            case ((true + "" == "true") ? 3 : 0):
            case ((false + "" == "false") ? 4 : 0):
        }
    }
}
