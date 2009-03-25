
class T15181d6 {
    T15181d6 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case (("" + Double.NaN == "NaN") ? 1 : 0):
            case (("" + 12.0 == "12.0") ? 2 : 0):
            case ((Double.NaN + "" == "NaN") ? 3 : 0):
            case ((12.0 + "" == "12.0") ? 4 : 0):
        }
    }
}
