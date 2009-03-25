
class T1521a8 {
    T1521a8 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((("1" == "2") == false) ? 1 : 0):
            case ((("1" != "2") == true) ? 2 : 0):
            case ((("1" == "2") != true) ? 3 : 0):
            case ((("1" != "2") != false) ? 4 : 0):
        }
    }
}
