
class T3102r11 {
    T3102r11 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((0.00000000000000000000000000000000000002350988491449805367214912435885053862149911421504883761540137648996591935440791942824034777004271745681762695312499999999f == 2.3509884e-38f) ? 1 : 0):
            case ((0.00000000000000000000000000000000000002350988491449805367214912435885053862149911421504883761540137648996591935440791942824034777004271745681762695312500000000f == 2.3509884e-38f) ? 2 : 0):
            case ((0.00000000000000000000000000000000000002350988491449805367214912435885053862149911421504883761540137648996591935440791942824034777004271745681762695312500000001f == 2.3509886e-38f) ? 3 : 0):
            case ((0.00000000000000000000000000000000000002350988631579651799696619528258012191141524549531077949191714824703420324419900211410094925668090581893920898437499999999f == 2.3509886e-38f) ? 4 : 0):
            case ((0.00000000000000000000000000000000000002350988631579651799696619528258012191141524549531077949191714824703420324419900211410094925668090581893920898437500000000f == 2.3509887e-38f) ? 5 : 0):
            case ((0.00000000000000000000000000000000000002350988631579651799696619528258012191141524549531077949191714824703420324419900211410094925668090581893920898437500000001f == 2.3509887e-38f) ? 6 : 0):
        }
    }
}
