
class T3102r9 {
    T3102r9 (){}
    void foo(int i) {
        switch (i) {
            case 0:
            case ((0.00000000000000000000000000000000000001175494280757364291727882991035766513322858992758990427682963118425003064965173038558532425668090581893920898437499999f == 1.1754942e-38f) ? 1 : 0):
            case ((0.000000000000000000000000000000000000011754942807573642917278829910357665133228589927589904276829631184250030649651730385585324256680905818939208984375f == 1.1754944e-38f) ? 2 : 0):
            case ((0.00000000000000000000000000000000000001175494280757364291727882991035766513322858992758990427682963118425003064965173038558532425668090581893920898437500001f == 1.1754944e-38f) ? 3 : 0):
        }
    }
}