
class T15173l2 {
    T15173l2 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case (((0x8000000000000000L / -1L) * -1L + (0x8000000000000000L % -1L)
            == 0x8000000000000000L) ? 1 : 0):
            case (((5L / 3L) * 3L + (5L % 3L) == 5L) ? 2 : 0):
            case (((5L / -3L) * -3L + (5L % -3L) == 5L) ? 3 : 0):
            case (((-5L / 3L) * 3L + (-5L % 3L) == -5L) ? 4 : 0):
            case (((-5L / -3L) * -3L + (-5L % -3L) == -5L) ? 5 : 0):
        }
    }
}
