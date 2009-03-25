
class T1521e1 {
    T1521e1 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case (((1!=2) == (!(1==2))) ? 1 : 0):
            case (((1!=1) == (!(1==1))) ? 2 : 0):
        }
    }
}
