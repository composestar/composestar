
class T15171f1 {
    T15171f1 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((Float.NaN * Float.NaN != Float.NaN * Float.NaN) ? 1 : 0):
        }
    }
}
