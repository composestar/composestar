
class T15181c4 {
    T15181c4 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case (("\uffff" == "" + '\uffff') ? 1 : 0):
            case (("\uffff" == '\uffff' + "") ? 2 : 0):
            case (("\uffff,\uffff" == '\uffff' + "," + '\uffff') ? 3 : 0):
        }
    }
}
