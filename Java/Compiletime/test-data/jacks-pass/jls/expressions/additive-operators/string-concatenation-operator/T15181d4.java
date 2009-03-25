
class T15181d4 {
    T15181d4 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case (("" + Double.MIN_VALUE == "4.9E-324") ? 1 : 0):
            case ((Double.MIN_VALUE + "" == "4.9E-324") ? 2 : 0):
        }
    }
}
