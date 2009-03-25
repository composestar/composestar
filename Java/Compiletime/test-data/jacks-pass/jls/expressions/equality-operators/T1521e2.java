
class T1521e2 {
    T1521e2 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case (((true!=true) == (!(true==true))) ? 1 : 0):
            case (((true!=false) == (!(true==false))) ? 2 : 0):
        }
    }
}
