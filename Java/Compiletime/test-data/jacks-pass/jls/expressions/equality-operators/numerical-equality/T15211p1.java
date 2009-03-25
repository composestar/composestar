
class T15211p1 {
    T15211p1 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case (((char)0xffff != (short)0xffff) ? 1 : 0):
        }
    }
}
