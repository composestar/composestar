
class T15211p2 {
    T15211p2 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case (((short)0xffff == (byte)0xff) ? 1 : 0):
        }
    }
}
