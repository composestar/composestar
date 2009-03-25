
interface Fish {
    int getNumberOfScales();
}
interface Piano {
    int getNumberOfScales();
}
class T814a4 implements Fish, Piano {
    // You can tune a piano, but can you tuna fish?
    public int getNumberOfScales() { return 91; }
}
