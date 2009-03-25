
class T1528paren1 {
    T1528paren1 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case (( ((((1)+2)+3)+4 == 10) ) ? 1 : 0):
        }
    }
}
