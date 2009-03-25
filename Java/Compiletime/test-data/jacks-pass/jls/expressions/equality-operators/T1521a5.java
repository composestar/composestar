
class T1521a5 {
    T1521a5 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((true == false == false) ? 1 : 0):
            case ((true != false == true) ? 2 : 0):
            case ((true == false != true) ? 3 : 0):
            case ((true != false != false) ? 4 : 0):
        }
    }
}
